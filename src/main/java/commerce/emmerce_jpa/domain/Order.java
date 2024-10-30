package commerce.emmerce_jpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long orderId;

    @CreatedDate
    private LocalDateTime orderDate;   // 주문 날짜

    private OrderStatus orderStatus;  // 주문 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Builder(builderMethodName = "createOrder")
    private Order(Long orderId, LocalDateTime orderDate, OrderStatus orderStatus, Member member) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.member = member;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
