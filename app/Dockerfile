FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

# Делаем gradlew исполняемым
RUN chmod +x gradlew

# Собираем fat-jar
RUN ./gradlew shadowJar

ENV PORT=7000

CMD ["java", "-jar", "build/libs/app-1.0.jar"]