package com.jane.ecommerce.config;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedissonClient() {
        // then
        assert redissonClient != null;
    }
}
