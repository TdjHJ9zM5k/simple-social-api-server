# Usa una base image con Maven e JDK 11
FROM maven:3.8.6-openjdk-11 AS build

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file di configurazione di Maven
COPY pom.xml .

# Scarica le dipendenze di Maven
RUN mvn dependency:go-offline

# Copia il codice sorgente
COPY src ./src

# Costruisci l'applicazione
RUN mvn package -DskipTests

# Usa una base image di JRE 11 per eseguire l'applicazione
FROM openjdk:11-jre

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR dalla fase di build
COPY --from=build /app/target/social-0.0.1-SNAPSHOT.jar app.jar

# Specifica il comando di esecuzione
ENTRYPOINT ["java", "-jar", "app.jar"]

# Esponi la porta su cui l'applicazione ascolterà
EXPOSE 8080
