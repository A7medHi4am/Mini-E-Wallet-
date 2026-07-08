FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw -q -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/mini-e-wallet-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
