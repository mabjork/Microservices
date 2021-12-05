FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD target/api_gateway-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=dev", "app.jar"]