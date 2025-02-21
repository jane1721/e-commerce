package com.jane.ecommerce.domain.outbox;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutboxTest {
    private Outbox outbox;

    @BeforeEach
    void setUp() {
        outbox = Outbox.init("Test Message");
    }

    @Test
    void testInit() {
        assertNotNull(outbox.getId());
        assertEquals("Test Message", outbox.getMessage());
        assertEquals(OutboxStatus.INIT, outbox.getStatus());
        assertEquals(0, outbox.getCnt());
    }

    @Test
    void testPublished() {
        outbox.published();
        assertEquals(OutboxStatus.PUBLISHED, outbox.getStatus());
    }

    @Test
    void testFailed() {
        outbox.failed();
        assertEquals(OutboxStatus.FAIL, outbox.getStatus());
    }

    @Test
    void testIncrementCnt() {
        outbox.incrementCnt();
        assertEquals(1, outbox.getCnt());
    }
}
