# 베이스 이미지 선택 (OpenJDK 21 사용)
FROM openjdk:17-jdk-alpine

VOLUME /app/data

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar ./app.jar

# 포트 노출 (스프링부트 기본 8080 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
