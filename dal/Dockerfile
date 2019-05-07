FROM openjdk:8-jre
COPY /target/*.jar /usr/app/app.jar
WORKDIR /usr/app
CMD [ "java", "-Dgrpc-server.port=${GPRC_SERVER_PORT}", "-jar", "app.jar" ]

