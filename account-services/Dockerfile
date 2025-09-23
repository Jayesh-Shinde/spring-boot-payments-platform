FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} account-services-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/account-services-1.0-SNAPSHOT.jar"]

# docker build --no-cache -t jayesh888/account-service:latest .
# docker push jayesh888/account-service:latest
