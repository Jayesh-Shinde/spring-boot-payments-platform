FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} notification-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/notification-service-1.0-SNAPSHOT.jar"]

# docker build -t jayesh888/notification-service:latest .
# docker push jayesh888/notification-service:latest