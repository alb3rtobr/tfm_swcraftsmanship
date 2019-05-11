FROM openjdk:8-jre
COPY /target/*.jar /usr/app/app.jar
WORKDIR /usr/app
CMD [ "java", "-Ddal.host=${DAL_HOST}", "-Ddal.port=${DAL_PORT}", "-jar", "app.jar" ]

