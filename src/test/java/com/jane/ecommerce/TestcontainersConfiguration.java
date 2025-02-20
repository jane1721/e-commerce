package com.jane.ecommerce;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("ecommerce")
			.withUsername("test")
			.withPassword("test");
	public static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.2"))
			.withExposedPorts(6379);
	public static final ConfluentKafkaContainer KAFKA_CONTAINER = new ConfluentKafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka:latest")
					.asCompatibleSubstituteFor("apache/kafka")
	);

	static {
		MYSQL_CONTAINER.start();
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