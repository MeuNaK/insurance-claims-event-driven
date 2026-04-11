FROM gradle:9.4.1-jdk21 AS build
WORKDIR /app

COPY ./settings.gradle .
COPY ./gradle.properties* .
COPY ./insurance-common-spring-boot-starter ./insurance-common-spring-boot-starter
COPY ./assessment-service ./assessment-service

RUN gradle :assessment-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/assessment-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
