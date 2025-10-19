FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} audit-services-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/audit-services-1.0-SNAPSHOT.jar"]

# docker build -t jayesh888/audit-service:latest .
# docker push jayesh888/audit-service:latest
