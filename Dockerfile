# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

ARG MODULE_NAME

COPY pom.xml .
COPY microservice-config/pom.xml microservice-config/
COPY microservice-eureka/pom.xml microservice-eureka/
COPY microservice-gateway/pom.xml microservice-gateway/
COPY microservice-citas/pom.xml microservice-citas/
COPY microservice-ecommerce/pom.xml microservice-ecommerce/
COPY microservice-notificaciones-recordatorios/pom.xml microservice-notificaciones-recordatorios/
COPY ${MODULE_NAME}/src ${MODULE_NAME}/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${MODULE_NAME} -am clean package -DskipTests && \
    cp ${MODULE_NAME}/target/${MODULE_NAME}-*.jar /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/America/Lima /etc/localtime && \
    echo "America/Lima" > /etc/timezone

COPY --from=builder /workspace/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
