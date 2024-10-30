package commerce.emmerce_jpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery")
@Entity
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long deliveryId;

    private String name;

    private String tel;

    private String email;

    private String city;

    private String street;

    private String zipcode;

    private DeliveryStatus deliveryStatus;  // 배송 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;


    @Builder(builderMethodName = "createDelivery")
    private Delivery(Long deliveryId, String name, String tel, String email, String city, String street,
                        String zipcode, DeliveryStatus deliveryStatus, OrderProduct orderProduct) {
        this.deliveryId = deliveryId;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.deliveryStatus = deliveryStatus;
        this.orderProduct = orderProduct;
    }

    public void updateStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
