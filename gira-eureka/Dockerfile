# 첫 번째 스테이지: 빌드 영역
# Gradle로 소스 코드를 빌드하기 위해 JDK 이미지를 사용합니다.
FROM openjdk:17-jdk-slim AS build

# 빌드 작업을 수행할 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 종속성 정보를 복사합니다.
# 캐싱을 활용하기 위해 소스 코드 전체가 아닌 필요한 파일만 우선 복사합니다.
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/

# Gradle Wrapper 실행 권한을 추가합니다.
# 실행 권한이 없으면 Permission Denied 오류가 발생합니다.
RUN chmod +x ./gradlew
RUN ls -l gradlew


# Gradle 종속성을 다운로드합니다.
# 종속성 캐싱이 가능하도록 빌드 스크립트가 변경되지 않은 경우 이전 데이터를 재사용합니다.
RUN ./gradlew dependencies --no-daemon

# 나머지 소스 코드 전체를 복사합니다.
COPY . /app

# Gradle로 빌드 작업을 수행합니다.
# 테스트는 제외하여 빌드 속도를 향상시킵니다.
RUN ./gradlew clean build -x test

# 두 번째 스테이지: 실행 영역
# 실행을 위한 JDK 이미지를 사용합니다. JDK가 필요 없는 경우 JRE로 변경 가능합니다.
FROM openjdk:17-jdk-slim

# 빌드 스테이지에서 생성된 결과물인 .jar 파일을 실행 환경으로 복사합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# ENTRYPOINT는 컨테이너 시작 시 반드시 실행되어야 하는 명령어입니다.
# Spring Boot 애플리케이션은 `java -jar`로 실행됩니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
