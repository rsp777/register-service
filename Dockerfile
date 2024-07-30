FROM openjdk:17-jdk-alpine
WORKDIR /register-service
MAINTAINER ravindra
COPY target/register-Release_1.0.jar  /register-service/register-Release_1.0.jar
ENTRYPOINT ["java","-jar","/register-service/register-Release_1.0.jar"]
EXPOSE 8082
