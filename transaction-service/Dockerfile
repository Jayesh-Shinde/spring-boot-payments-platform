FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} transaction-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/transaction-service-1.0-SNAPSHOT.jar"]

# docker build -t jayesh888/transaction-service:latest .
# docker push jayesh888/transaction-service:latest