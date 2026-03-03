# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY . /app
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/Employee_management-1.0-SNAPSHOT.jar ./Employee_management.jar

# Tell the app it's running in Docker (headless)
ENV DOCKER=1

CMD ["java", "-jar", "Employee_management.jar"]