package com.jane.ecommerce.domain.outbox;

import com.jane.ecommerce.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "outbox")
@Entity
public class OutboxEntity extends BaseEntity {
    @Id
    private String id;

    @Column(columnDefinition = "longtext")
    private String message;

    private OutboxStatus status;

    private int cnt;

    public OutboxEntity(String id,
                        String message,
                        OutboxStatus status,
                        int cnt) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.cnt = cnt;
    }

    public Outbox toDomain() {
        return Outbox.builder()
                .id(this.id)
                .message(this.message)
                .status(this.status)
                .cnt(this.cnt)
                .build();
    }

    public static OutboxEntity toEntity(Outbox outbox) {
        return new OutboxEntity(outbox.getId(),
                outbox.getMessage(),
                outbox.getStatus(),
                outbox.getCnt());
    }
}
