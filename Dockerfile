FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml./
RUN ./mvnw dependency: go-offline
COPY ./src ./src
RUN ./mvnw clean install

FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine
WORKDIR /opt/app

EXPOSE 8080

COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar

ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]