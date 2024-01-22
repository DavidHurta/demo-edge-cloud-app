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
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
ENTRYPOINT java -jar $APP_HOME/build/libs/$JAR_NAME