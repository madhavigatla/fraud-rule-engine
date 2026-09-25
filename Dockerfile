# Build stage (Uses a proper Maven development image)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Compile and package
COPY src ./src
RUN mvn package -DskipTests

# Run stage (Uses an optimized, Apple Silicon-compatible runtime)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/fraud-rule-engine-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
