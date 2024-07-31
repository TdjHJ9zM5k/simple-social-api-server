# Stage 1: Build the application with Maven
FROM maven:3.6.3-openjdk-17-slim as builder

WORKDIR /app
COPY pom.xml .
# Use this optimization to cache the local dependencies. Works as long as the POM doesn't change
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package

# Stage 2: Create the production image
FROM openjdk:17-jdk-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/*.jar /app.jar

# Copy the H2 database file to the production image
COPY mydatabase.mv.db /data/mydatabase.mv.db

# Run the web service on container startup.
CMD ["java", "-jar", "/app.jar"]
