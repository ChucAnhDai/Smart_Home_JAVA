# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application, skipping tests to speed up the process
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port
EXPOSE 8081
# Profile set to prod to use application-prod.properties
ENV SPRING_PROFILES_ACTIVE=prod
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
