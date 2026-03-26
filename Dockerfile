# Etapa de construcción (Build Stage)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias para acelerar construcciones posteriores
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución (Runtime Stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Dejamos que el puerto se defina por la variable de entorno PORT que Render inyecta
# Ejecutar la aplicación forzando el puerto que nos asigne Render
ENTRYPOINT ["java", "-Dserver.port=${PORT:10000}", "-jar", "app.jar"]
