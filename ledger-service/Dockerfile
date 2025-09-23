FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ledger-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ledger-service-1.0-SNAPSHOT.jar"]

# docker build --no-cache -t jayesh888/ledger-service:latest .
# docker push jayesh888/ledger-service:latest