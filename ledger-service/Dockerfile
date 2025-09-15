FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ledger-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ledger-service-1.0-SNAPSHOT.jar"]

# docker build -t ledger-service:1.0 .
# docker tag ledger-service:1.0 jayesh888/ledger-service:2.0.0
# docker push jayesh888/ledger-service:2.0.0