# Этап сборки
FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew shadowJar

# Этап запуска
FROM openjdk:21-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/app-1.0.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]