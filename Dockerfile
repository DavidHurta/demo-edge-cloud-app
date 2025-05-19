#
#  Author: David Hurta (xhurta04)
#  Project: DIP
#
# Structure inspired by https://stackoverflow.com/a/74951353 by Abdelsalam Megahed (https://stackoverflow.com/users/11428614/abdelsalam-megahed)

# Build Stage: Create a JAR file that contains all dependciess
FROM gradle:jdk21 AS build
WORKDIR /usr/app/
COPY . .
RUN gradle shadowJar

# Final Stage: Create the final image
FROM amazoncorretto:21
WORKDIR /usr/app/
COPY --from=build /usr/app/build/libs/demo-sensor-1.0-SNAPSHOT-all.jar app.jar
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
CMD ["--help"]