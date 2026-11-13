FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copier les fichiers Maven et le code source
COPY pom.xml .
COPY src ./src

# Télécharger les dépendances
RUN mvn dependency:go-offline

# Compiler et générer le JAR
RUN mvn clean package -DskipTests


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
