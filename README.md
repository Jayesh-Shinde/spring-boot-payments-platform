# spring-boot-payments-platform

A fintech-style payments platform built with Java Spring Boot microservices. This project demonstrates double-entry
accounting,
event-driven architecture with Kafka, and deployment readiness with Docker and Kubernetes.

## Features

- Account management (create, fetch, balances)
- Double-entry ledger system (journals & journal lines)
- Event-driven communication via **Apache Kafka**
- PostgreSQL with multiple databases (Accounts, Ledger)
- Transaction management with Spring `@Transactional`
- Docker Compose setup (Postgres + Kafka + Zookeeper)

## Tech Stack

- **Java 22**
- **Spring Boot 3.5.4** (REST, JPA, Validation)
- **PostgreSQL 16**
- **Apache Kafka**
- **Docker Compose** (local infra)
- **Maven (multi-module project)**

### pgsql

Accounts DB (`accountsdb`)

```sql
-- public.accounts definition

-- Drop table

-- DROP TABLE public.accounts;

CREATE TABLE public.accounts (
id uuid NOT NULL,
"name" varchar(255) NOT NULL,
currency varchar(255) NOT NULL,
"type" varchar(255) NOT NULL,
created_at timestamptz(6) DEFAULT CURRENT_TIMESTAMP NULL,
CONSTRAINT accounts_pkey PRIMARY KEY (id)
);
```

Below tables under Ledger DB (ledgerdb)

```sql

-- public.journals definition

-- Drop table

-- DROP TABLE public.journals;

CREATE TABLE public.journals (
id uuid NOT NULL,
created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
idempotency_key varchar(255) NULL,
reference varchar(255) NULL,
status varchar(255) DEFAULT 'POSTED'::character varying NULL,
CONSTRAINT journals_idempotency_key_key UNIQUE (idempotency_key),
CONSTRAINT journals_pkey PRIMARY KEY (id)
);

-- public.journal_lines definition

-- Drop table

-- DROP TABLE public.journal_lines;

CREATE TABLE public.journal_lines (
id uuid NOT NULL,
journal_id uuid NULL,
account_id uuid NOT NULL,
amount numeric(38, 2) NOT NULL,
entry_type varchar(255) NULL,
CONSTRAINT check_amount_is_positive CHECK ((amount > (0)::numeric)),
CONSTRAINT journal_lines_entry_type_check CHECK (((entry_type)::text = ANY (
ARRAY[('DEBIT'::character varying)::text, ('CREDIT'::character varying)::text]))),
CONSTRAINT journal_lines_pkey PRIMARY KEY (id)
);

-- public.journal_lines foreign keys

ALTER TABLE public.journal_lines ADD CONSTRAINT journal_lines_journal_id_fkey FOREIGN KEY (journal_id) REFERENCES
public.journals(id);


```

### Setup and Run

- Clone the repository :
    - git clone https://github.com/Jayesh-Shinde/spring-boot-payments-platform.git
      cd spring-boot-payments-platform

- Start infrastructure (Postgres + Kafka):
    - docker compose -f postgres-kafka-dockercompose.yml up -d

- Set environment variables:

    - export DB_USERNAME= put your username
    - export DB_PASSWORD= put your password

- Run services
    - mvn clean install
    - cd accounts-service && mvn spring-boot:run
    - cd ledger-service && mvn spring-boot:run

### How to create image of your services

- Docker file setup
    - At the root of your project run mvn clean install
    - Go to each microservice folder and create a docker file Ex: sample is below

```dockerfile

FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ledger-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ledger-service-1.0-SNAPSHOT.jar"]

# steps to build and push image
# docker build -t ledger-service:1.0 .

# docker tag ledger-service:1.0 <your docker registry>/ledger-service:2.0.0

# docker push <your docker registry>/ledger-service:2.0.0

```

- Deploy to kubernetes
    - kubernetes folder contain the service files to deploy supporting all components
        1) postgresql for relational database of spring boot services
        2) kafka for sharing messages between the microservices
        3) keycloak service for generating and validating access token
        4) postgresql for persisting data of keycloak
        5) account-service
        6) ledger-service
        7) networking ingress service to expose account service / ledger service / keycloak to outside
           of minikube service
        8) Use of helm //TODO


- Added a sample github workflow which can run build and publish individual microservices to dockerhub
    - Open your repository on GitHub.
    - Click Settings → Actions → Runners → New self-hosted runner.
    - Choose your OS and architecture.
    - GitHub will generate a download URL and setup commands specific to your system.
    - After following above steps , Go back to GitHub → Settings → Actions → Runners.
    - You should see your runner listed as Online.
    - Add a simple GitHub Actions workflow in .github/workflows/ and specify your runner with a
      runs-on label:
    - Note.github/workflow directory should be at the root of repo

```
jobs:
  test:
    runs-on: self-hosted
    steps:
      - name: 'Checkout repository code'
        uses: actions/checkout@v4
      - name: 'Build and push docker images'
        run: <rest of command , refer file at root of this repo>
```
  




