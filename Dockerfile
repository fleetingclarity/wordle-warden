# Use a Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/wordle-warden-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
