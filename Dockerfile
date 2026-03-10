# ---------------------------
# Stage 1: Build the JAR
# ---------------------------
FROM eclipse-temurin:21-jdk AS build

# Install dependencies for Maven build and GUI
RUN apt-get update && \
    apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the fat/shaded JAR
RUN mvn clean package -DskipTests

# ---------------------------
# Stage 2: Runtime image
# ---------------------------
FROM eclipse-temurin:21-jre

# Install GUI dependencies
RUN apt-get update && \
    apt-get install -y libgtk-3-0 libgbm1 libx11-6 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built JAR and JavaFX SDK
COPY --from=build /app/target/Employee_management-1.0-SNAPSHOT.jar ./Employee_management.jar
COPY --from=build /opt/javafx-sdk-21 /opt/javafx-sdk-21

# Set DISPLAY for X11 forwarding
ENV DISPLAY=:0

# Keep container alive if app exits
CMD ["java", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "Employee_management.jar"]