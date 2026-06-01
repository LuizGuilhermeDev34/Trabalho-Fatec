# ─────────────────────────────────────────────────────
# Stage 1: Build com Maven
# ─────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia o pom.xml e baixa dependências (cache de camada)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────
# Stage 2: Imagem final mínima (JRE only)
# ─────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -S castrapet && adduser -S castrapet -G castrapet
USER castrapet

# Copia o JAR gerado
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta da API
EXPOSE 8080

# Health check interno
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Inicia o Spring Boot com perfil de produção
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
