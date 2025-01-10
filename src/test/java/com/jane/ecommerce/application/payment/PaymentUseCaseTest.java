package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.ServerApplication;
import com.jane.ecommerce.domain.item.ItemRepository;
import com.jane.ecommerce.domain.order.OrderRepository;
import com.jane.ecommerce.domain.payment.PaymentRepository;
import com.jane.ecommerce.domain.user.UserRepository;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ServerApplication.class)
@Sql(scripts = "/test-data.sql") // 테스트 데이터를 삽입하는 SQL 스크립트
public class PaymentUseCaseTest {


    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testProcessPayment_Success() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("1", "1", "CARD");

        // when
        PaymentCreateResponse response = paymentUseCase.processPayment(paymentRequest);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(paymentRepository.findById(response.getId())).isPresent();
        assertThat(orderRepository.findById(1L).get().getStatus()).isEqualTo("COMPLETED");
        assertThat(userRepository.findById(1L).get().getBalance()).isEqualTo(50000L); // 잔액 검증
    }

    @Test
    void testProcessPayment_Failure_InsufficientBalance() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("2", "2", "CARD"); // 잔액 부족한 사용자

        // when
        PaymentCreateResponse response = paymentUseCase.processPayment(paymentRequest);

        // then
        assertThat(response.getStatus()).isEqualTo("CANCELLED");
        assertThat(orderRepository.findById(2L).get().getStatus()).isEqualTo("CANCELLED");
        assertThat(itemRepository.findById(1L).get().getStock()).isEqualTo(10); // 재고 복구 검증
    }
}
