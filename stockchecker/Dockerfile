FROM openjdk:8-jre
COPY /target/*.jar /usr/app/app.jar
WORKDIR /usr/app
CMD [ "java", "-Dspring.cloud.stream.kafka.binder.brokers=${KAFKA_SERVER}:${KAFKA_PORT}", "-Ddal.host=${DAL_HOST}", "-Ddal.port=${DAL_PORT}", "-jar", "app.jar" ]

