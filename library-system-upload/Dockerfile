# ---------- Stage 1: build the jar ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: run it ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar

# Render gives the app a PORT env var; fall back to 8080 locally
ENV PORT=8080
# Keep memory low enough for a 512 MB free instance
ENV JAVA_OPTS="-Xmx384m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
