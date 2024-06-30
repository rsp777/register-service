FROM openjdk:17-jdk-alpine
WORKDIR /register-service
MAINTAINER ravindra
RUN mvn clean package
COPY target/register-0.0.1-SNAPSHOT.jar  /register-service/register-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","register-0.0.1-SNAPSHOT.jar"]
EXPOSE 8082
