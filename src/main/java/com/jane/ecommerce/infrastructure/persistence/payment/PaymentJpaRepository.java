package com.jane.ecommerce.infrastructure.persistence.payment;

import com.jane.ecommerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
