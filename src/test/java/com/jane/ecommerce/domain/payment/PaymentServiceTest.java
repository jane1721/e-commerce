package com.jane.ecommerce.domain.payment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    // 결제 데이터 생성 성공
    @Test
    public void testCreatePayment_Success() {
        // given
        Long orderId = 1L;
        String method = "CREDIT_CARD";

        Order mockOrder = Order.of(orderId, null, BigDecimal.valueOf(1000L), BigDecimal.ZERO, null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Payment payment = paymentService.createPayment(orderId, method);

        // then
        assertNotNull(payment);
        assertEquals(mockOrder, payment.getOrder());
        assertEquals(BigDecimal.valueOf(1000), payment.getAmount());
        assertEquals(method, payment.getMethod());
        verify(orderRepository, times(1)).findById(orderId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // 존재하지 않는 주문으로 결제 데이터 생성 실패
    @Test
    public void testCreatePayment_OrderNotFound() {
        // given
        Long orderId = 1L;
        String method = "CREDIT_CARD";

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> paymentService.createPayment(orderId, method));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(orderRepository, times(1)).findById(orderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    // 결제 상태 조회 성공
    @Test
    public void testGetPaymentStatus_Success() {
        // given
        Long paymentId = 1L;

        Payment mockPayment = Payment.of(paymentId, null, BigDecimal.ZERO, null);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(mockPayment));

        // when
        Payment payment = paymentService.getPaymentStatus(paymentId);

        // then
        assertNotNull(payment);
        assertEquals(paymentId, payment.getId());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    // 존재하지 않는 결제 상태 조회 실패
    @Test
    public void testGetPaymentStatus_NotFound() {
        // given
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> paymentService.getPaymentStatus(paymentId));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(paymentRepository, times(1)).findById(paymentId);
    }
}
