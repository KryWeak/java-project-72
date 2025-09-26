# Этап сборки
FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean # Очистка кэша Gradle
RUN ./gradlew shadowJar --no-daemon # Отключаем daemon для стабильности в Docker

# Этап запуска
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/app-1.0.jar app.jar
ENV PORT=10000
EXPOSE 10000
CMD ["java", "-jar", "app.jar"]