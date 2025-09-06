# Dockerfile para Spring Boot API - Otimizado para Render
FROM eclipse-temurin:17-jdk-alpine AS build

# Instalar Maven
RUN apk add --no-cache maven

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (para cache de dependências)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Baixar dependências (camada separada para cache)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Construir aplicação
RUN mvn clean package -DskipTests

# Estágio de runtime - imagem menor
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 spring && adduser -u 1001 -G spring -s /bin/sh -D spring

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Dar permissões adequadas
RUN chown spring:spring /app/app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Configurações JVM otimizadas para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -Djava.security.egd=file:/dev/./urandom"

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
