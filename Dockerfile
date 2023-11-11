FROM maven:3.9.0-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package state
#
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/letschat-backend-0.0.1-SNAPSHOT.jar letschat-backend-0.0.1-SNAPSHOT.jar
# ENV PORT=9200
ENTRYPOINT ["java", "-jar", "letschat-backend-0.0.1-SNAPSHOT.jar"]
