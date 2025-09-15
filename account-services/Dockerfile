FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} account-services-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/account-services-1.0-SNAPSHOT.jar"]

# docker build -t account-service:1.0 .
# docker tag account-service:1.0 jayesh888/account-service:3.0.0
# docker push jayesh888/account-service:3.0.0
