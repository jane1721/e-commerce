package com.jane.ecommerce;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

// 테스트 환경을 위한 Docker 컨테이너를 설정
@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		// MySQLContainer 를 사용해 mysql:8.0 이미지로 MySQL 컨테이너를 생성한다.
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("ecommerce")
			.withUsername("test")
			.withPassword("test");
		MYSQL_CONTAINER.start();

		/*
		테스트 컨테이너는 랜덤으로 포트가 설정되기 때문에
		컨테이너를 시작한 후, 시스템 프로퍼티를 MySQL 컨테이너의 연결 정보로 설정하여
		Spring Boot 애플리케이션이 해당 데이터베이스에 연결할 수 있도록 한다.
		 */
		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

		// GenericContainer 를 사용해 redis:7.0 이미지로 Redis 컨테이너를 생성한다.
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.0"))
			.withExposedPorts(6379);
		REDIS_CONTAINER.start();

		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
	}

	@PreDestroy
	public void preDestroy() {
		// 애플리케이션 종료 시 실행 중인 MySQL 과 Redis 컨테이너를 종료한다.
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
	}
}