FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:17-jre

WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring --home-dir /app spring
COPY --from=build --chown=spring:spring /workspace/target/movieapp-0.0.1-SNAPSHOT.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]