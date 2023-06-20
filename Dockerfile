#Build
FROM maven:3.8.3-openjdk-11-slim AS builder
WORKDIR /app
COPY . .
RUN mvn clean install

#Create image
FROM openjdk:11
VOLUME /tmp
EXPOSE 8081
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]