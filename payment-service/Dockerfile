FROM openjdk:17-jdk-slim

LABEL maintainer="ecommerce-team"

WORKDIR /app

COPY target/payment-service-0.0.1-SNAPSHOT.jar payment-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "payment-service.jar"]
