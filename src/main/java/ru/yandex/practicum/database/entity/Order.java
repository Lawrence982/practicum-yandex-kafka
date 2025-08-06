package ru.yandex.practicum.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Order extends AuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(name = "order_date")
    private Instant orderDate;

    @PrePersist
    public void prePersist() {
        if (orderDate == null) {
            orderDate = Instant.now();
        }
    }

}
