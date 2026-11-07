FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Télécharger les dépendances
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copier le code source
COPY src ./src

# Compiler et générer le JAR
RUN ./mvnw clean package -DskipTests


# ==========================================
# STAGE 2 : RUNTIME
# ==========================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copier uniquement le JAR généré
COPY --from=build /app/target/*.jar app.jar

# Port de l'application
EXPOSE 8080

# Démarrer Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
