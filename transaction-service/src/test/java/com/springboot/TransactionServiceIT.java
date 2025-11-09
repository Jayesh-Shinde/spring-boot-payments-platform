package com.springboot;

import com.springboot.dto.events.TransactionEvent;
import com.springboot.dto.requests.TransactionRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.AccountDTO;
import com.springboot.dto.response.TransactionResponse;
import com.springboot.entity.Transaction;
import com.springboot.feignclients.AccountClient;
import com.springboot.feignclients.LedgerClient;
import com.springboot.repository.TransactionRepository;
import com.springboot.service.TransactionService;
import com.springboot.service.TransactionServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
//mvn -pl transaction-service -Dtest=TransactionServiceIT test
public class TransactionServiceIT {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2")
            .asCompatibleSubstituteFor("apache/kafka"));

    @MockBean
    private AccountClient accountClient;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @MockBean
    private LedgerClient ledgerClient;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    private static KafkaConsumer<String, TransactionEvent> consumer;

    @BeforeAll
    static void setupKafkaConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of("transactions.created"));
    }

    @AfterAll
    static void closeKafkaConsumer() {
        if (consumer != null) consumer.close();
    }

    @Test
    void shouldCreateTransactionAndPublishEvent() {
        UUID toAccount = UUID.randomUUID();
        UUID fromAccount = UUID.randomUUID();
        when(accountClient.getAccountsById(toAccount)).thenReturn(new AccountDTO(
                toAccount,
                "Manva",
                "JPY",
                "SAVINGS",
                null));
        when(accountClient.getAccountsById(fromAccount)).thenReturn(
                new AccountDTO(
                        fromAccount,
                        "Jayesh",
                        "JPY",
                        "SAVINGS",
                        null)
        );
        when(ledgerClient.getAccountBalance(fromAccount)).thenReturn(
                new ResponseEntity<>(
                        new AccountBalances(fromAccount,
                                BigDecimal.valueOf(1000)),
                        HttpStatusCode.valueOf(200))
        );

        TransactionRequest request = new TransactionRequest();
        request.setFromAccountID(fromAccount);
        request.setToAccountId(toAccount);
        request.setAmount(BigDecimal.valueOf(100));
        Transaction transaction = new Transaction(request.getFromAccountID(), request.getToAccountId(), request.getAmount(), "SUCCESS");
        TransactionResponse response = transactionService.createTransaction(request, "1");
        System.out.println("response:");
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();

        var byId = transactionRepository.findById(response.getId());

        assertThat(byId).isPresent();

        ConsumerRecords<String, TransactionEvent> poll = consumer.poll(Duration.ofSeconds(5));

        boolean found = StreamSupport.stream(poll.records("transactions.created").spliterator(), false)
                .anyMatch(i ->
                        i.value().getId().equals(response.getId()));

        assertThat(found).as(
                "Kafka should contain the transaction id in the event message of :" + response.getId()
        ).isTrue();


    }


}
