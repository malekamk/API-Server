# Use a lightweight Java image
FROM openjdk:21-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/Kganyamaleka-1.0-SNAPSHOT.jar /app/Kganyamaleka.jar

# Expose the port your app listens on
EXPOSE 5050

# Command to run your JAR file
CMD ["java", "-jar", "Kganyamaleka.jar"]
