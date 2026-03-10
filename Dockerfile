# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="mehdizaidane"
ENV DISPLAY=host.docker.internal:0.0

RUN apt-get update && \
    apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK 21
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip \

WORKDIR /app
COPY pom.xml .
COPY . /app
COPY src ./src

RUN mvn package -DskipTests

RUN ls -l target

COPY --from=build /app/target/Employee_management-1.0-SNAPSHOT.jar ./Employee_management.jar

CMD ["java", "-jar", "Employee_management.jar"]