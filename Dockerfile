# -------- Build Stage --------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to cache dependencies)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Package application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# -------- Run Stage --------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT dynamically
ENV PORT=8080
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
