FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/BestWorldWideDeals-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "BestWorldWideDeals-0.0.1-SNAPSHOT.jar"]
