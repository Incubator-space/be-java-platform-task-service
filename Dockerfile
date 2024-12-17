FROM openjdk:17.0.2-slim-bullseye

COPY task-service-src/target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]