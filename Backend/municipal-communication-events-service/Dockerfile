FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

# In environments without maven wrapper, maven command can be used or packaged jar
# For multi-stage docker build:
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target
COPY --from=build ${DEPENDENCY}/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

