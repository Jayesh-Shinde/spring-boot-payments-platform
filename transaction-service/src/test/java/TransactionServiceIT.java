import com.springboot.dto.events.TransactionEvent;
import com.springboot.feignclients.AccountClient;
import com.springboot.feignclients.LedgerClient;
import com.springboot.service.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.util.Properties;

@SpringBootTest
@Testcontainers
public class TransactionServiceIT {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer("bitnami/kafka:3.7");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Mock
    private AccountClient accountClient;

    @Mock
    private LedgerClient ledgerClient;

    @InjectMocks
    private TransactionService transactionService;

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
    }

    @AfterAll
    static void closeKafkaConsumer() {
        if (consumer != null) consumer.close();
    }

    
}
