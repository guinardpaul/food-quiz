# -------- STAGE 1 : build Maven --------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# copier tout le projet multi-module
COPY . .

# build uniquement le module api (et ses deps)
RUN mvn -pl 'food-quiz-api' -am clean package -DskipTests


# -------- STAGE 2 : runtime --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# récupérer le jar buildé
COPY --from=build /app/food-quiz-api/target/food-quiz-api-*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]