FROM openjdk:21-jdk-slim
WORKDIR /opt
COPY ./target/synchronization-service.jar /opt/
ENTRYPOINT ["java", "-server", "-Xms512m", "-jar", "/opt/synchronization-service.jar"]