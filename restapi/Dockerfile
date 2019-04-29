FROM maven as builder
COPY . /code/
WORKDIR /code
RUN mvn package

FROM openjdk:8-jre
COPY --from=builder /code/target/*.jar /usr/app/app.jar
WORKDIR /usr/app
CMD [ "java", "-Dserver.port=${SERVER_PORT}", "-Dspring.cloud.stream.kafka.binder.brokers=${KAFKA_SERVER}:${KAFKA_PORT}", "-jar", "app.jar" ]

