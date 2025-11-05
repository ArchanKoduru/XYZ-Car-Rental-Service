# Use official Java 21 JDK
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Gradle build outputs
COPY build/libs/xyz-car-rental-1.0.0.jar app.jar

# Expose default port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]