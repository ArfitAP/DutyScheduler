# ---------- Stage 1: Build ----------
FROM maven:3.9.12-eclipse-temurin-25-alpine AS builder

# Set working directory
WORKDIR /app

# Copy only pom.xml first (for dependency caching)
COPY pom.xml .

# Download dependencies (cached layer if pom.xml unchanged)
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]