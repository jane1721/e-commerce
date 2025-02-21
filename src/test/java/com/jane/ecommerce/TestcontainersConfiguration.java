package com.jane.ecommerce;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

// 테스트 환경을 위한 Docker 컨테이너를 설정
@Configuration
class TestcontainersConfiguration {

	// MySQLContainer 를 사용해 mysql:8.0 이미지로 MySQL 컨테이너를 생성한다.
	public static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("ecommerce")
			.withUsername("test")
			.withPassword("test");
	// GenericContainer 를 사용해 redis:7.0 이미지로 Redis 컨테이너를 생성한다.
	public static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.2"))
			.withExposedPorts(6379);
	public static final ConfluentKafkaContainer KAFKA_CONTAINER = new ConfluentKafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka:latest")
					.asCompatibleSubstituteFor("apache/kafka")
	);

	static {
		MYSQL_CONTAINER.start();

		/*
		테스트 컨테이너는 랜덤으로 포트가 설정되기 때문에
		컨테이너를 시작한 후, 시스템 프로퍼티를 MySQL 컨테이너의 연결 정보로 설정하여
		Spring Boot 애플리케이션이 해당 데이터베이스에 연결할 수 있도록 한다.
		 */
		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

		REDIS_CONTAINER.start();
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());

		KAFKA_CONTAINER
				.withEnv("KAFKA_LISTENERS", "PLAINTEXT://:9092,BROKER://:9093,CONTROLLER://:9094")
				.start();
		System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());
	}
}