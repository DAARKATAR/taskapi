# Etapa de construcción (Build Stage)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución (Runtime Stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Obligar a Spring Boot a usar el puerto que Render le mande, por defecto 10000
ENTRYPOINT ["java", "-Dserver.port=${PORT:10000}", "-jar", "app.jar"]
