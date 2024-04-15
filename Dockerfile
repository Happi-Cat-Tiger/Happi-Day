FROM openjdk:17-jdk-slim
COPY build/libs/Happi_Day-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]