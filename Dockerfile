#
# Build stage
#
#FROM maven:3.8.3-openjdk-17 AS build
#LABEL authors="erick.rangel"
##COPY src /home/app/src
##COPY pom.xml /home/app
#COPY target/com.mobile-app-ws-0.0.1-SNAPSHOT.jar mobile_app_ws.jar
##RUN mvn -f /home/app/pom.xml clean package
##EXPOSE 8080
#ENTRYPOINT ["java","-jar","/mobile_app_ws.jar"]
# Use the OpenJDK 17 image based on Alpine Linux for a minimal size
FROM openjdk:17-jdk-alpine

# Optional: Set a maintainer or label for the image
LABEL maintainer="erick.rangel@globant.com"

# Volume pointing to /tmp, useful for storing temporary files by the application
VOLUME /tmp

# Expose port 8080 for your application
EXPOSE 8080

# Add the application's jar to the container
ADD target/*.jar app.jar

# Set environment variables for Java options (customize as needed)
ENV JAVA_OPTS=""

# Set the entry point to execute the jar
# Note: Java 17 no longer requires the workaround for SecureRandom (Djava.security.egd=file:/dev/./urandom)
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]


