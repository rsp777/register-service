FROM openjdk:17-jdk-alpine
WORKDIR /register-service
MAINTAINER ravindra
COPY target/register-0.0.1-SNAPSHOT.jar  /register-service/register-Release_1.0.jar
ENTRYPOINT ["java","-jar","register-Release_1.0.jar"]
EXPOSE 8082
