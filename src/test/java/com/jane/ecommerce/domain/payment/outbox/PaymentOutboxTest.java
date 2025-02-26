package com.jane.ecommerce.domain.payment.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentOutboxTest {

    private PaymentOutbox paymentOutbox;

    @BeforeEach
    void setup() {
        paymentOutbox = PaymentOutbox.init("Test Message");
    }

    @Test
     void testInit() {
        assertNotNull(paymentOutbox.getId());
        assertEquals("Test Message", paymentOutbox.getMessage());
        assertEquals(PaymentOutboxStatus.INIT, paymentOutbox.getStatus());
        assertEquals(0, paymentOutbox.getCount());
    }

    @Test
    void testPublished() {
        paymentOutbox.published();
        assertEquals(PaymentOutboxStatus.PUBLISHED, paymentOutbox.getStatus());
    }

    @Test
    void testFailed() {
        paymentOutbox.failed();
        assertEquals(PaymentOutboxStatus.FAIL, paymentOutbox.getStatus());
    }

    @Test
    void testIncrementCnt() {
        paymentOutbox.incrementCount();
        assertEquals(1, paymentOutbox.getCount());
    }
}
