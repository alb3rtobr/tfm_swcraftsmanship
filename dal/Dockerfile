FROM openjdk:8-jre
COPY /target/*.jar /usr/app/app.jar
WORKDIR /usr/app
CMD [ "java", "-Dgrpc-server.port=${GRPC_SERVER_PORT}", "-Dspring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/mybbdd", "-jar", "app.jar" ]

