FROM openjdk:11-jre-alpine
WORKDIR /app
EXPOSE 8081
ADD ./build/libs/server.jar .
CMD ["java", "-jar", "server.jar"]