FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=0 /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 