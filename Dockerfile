#
#  Author: David Hurta (xhurta04)
#  Project: DIP
#
# Structure inspired by https://stackoverflow.com/a/74951353
FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . . 
RUN gradle shadowJar
FROM openjdk:latest
ENV JAR_NAME=demo-sensor-1.0-SNAPSHOT-all.jar
WORKDIR /usr/app/
COPY --from=BUILD /usr/app/build/libs/demo-sensor-1.0-SNAPSHOT-all.jar app.jar
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
CMD ["--help"]