# Stage 1: build the jar (runs the unit tests too; the build fails if a test fails)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package

# Stage 2: small runtime image with only Java and the jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/guru-studio-backend.jar app.jar
# Render sets PORT itself. 8080 is the local default.
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
