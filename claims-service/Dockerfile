FROM gradle:9.4.1-jdk21 AS build
WORKDIR /app
COPY . .

COPY ./settings.gradle .
COPY ./gradle.properties* .
COPY ./insurance-common-spring-boot-starter ./insurance-common-spring-boot-starter
COPY ./claims-service ./claims-service

RUN gradle :claims-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/claims-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
