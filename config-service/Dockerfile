FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD target/config_service-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]