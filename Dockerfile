#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
LABEL authors="erick.rangel"
#COPY src /home/app/src
#COPY pom.xml /home/app
COPY target/com.mobile-app-ws-0.0.1-SNAPSHOT.jar mobile_app_ws.jar
#RUN mvn -f /home/app/pom.xml clean package
#EXPOSE 8080
ENTRYPOINT ["java","-jar","/mobile_app_ws.jar"]