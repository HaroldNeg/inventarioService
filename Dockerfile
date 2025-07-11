FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/inventario-service-1.0.0.jar inventario-service.jar
EXPOSE 1000
ENTRYPOINT ["java", "-jar", "inventario-service.jar", "--spring.profiles.active=docker"]
