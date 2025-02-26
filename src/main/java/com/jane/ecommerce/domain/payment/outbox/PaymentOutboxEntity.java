package com.jane.ecommerce.domain.payment.outbox;

import com.jane.ecommerce.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "payment_outbox")
@Entity
public class PaymentOutboxEntity extends BaseEntity {
    @Id
    private String id;

    @Column(columnDefinition = "longtext")
    private String message;

    private PaymentOutboxStatus status;

    private int count;

    public PaymentOutboxEntity(String id, String message, PaymentOutboxStatus status, int count) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.count = count;
    }

    public PaymentOutbox toDomain() {
        return PaymentOutbox.builder()
                .id(this.id)
                .message(this.message)
                .status(this.status)
                .count(this.count)
                .build();
    }

    public static PaymentOutboxEntity from(PaymentOutbox outbox) {
        return new PaymentOutboxEntity(
                outbox.getId(),
                outbox.getMessage(),
                outbox.getStatus(),
                outbox.getCount()
        );
    }
}
