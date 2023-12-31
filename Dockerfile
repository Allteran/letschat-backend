FROM maven:3.9.0-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package state
#
FROM openjdk:19
COPY --from=build /target/letschat-backend-0.0.1-SNAPSHOT.jar letschat-backend-0.0.1-SNAPSHOT.jar
# ENV PORT=9200
EXPOSE 8200
ENTRYPOINT ["java", "-jar", "letschat-backend-0.0.1-SNAPSHOT.jar"]
