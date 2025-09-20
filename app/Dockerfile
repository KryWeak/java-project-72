# Базовый образ с Gradle 8 и JDK 21
FROM gradle:8.3-jdk21

WORKDIR /app

# Копируем весь проект
COPY . .

# Делаем gradlew исполняемым
RUN chmod +x gradlew

# Собираем fat-jar
RUN ./gradlew shadowJar

# Переменная окружения для порта (Render использует PORT)
ENV PORT=7000

# Запуск приложения
CMD ["java", "-jar", "build/libs/app-1.0.jar"]