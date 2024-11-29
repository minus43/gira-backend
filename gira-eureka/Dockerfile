# 빌드 스테이지
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# 소스 코드 복사 및 빌드
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test



