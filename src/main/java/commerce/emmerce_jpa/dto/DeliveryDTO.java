package commerce.emmerce_jpa.dto;

import commerce.emmerce_jpa.domain.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeliveryDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryReq {
        // 주문자 정보
        private String name;
        private String tel;
        private String email;
        // 배송지 정보
        private String city;
        private String street;
        private String zipcode;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusReq {
        private DeliveryStatus deliveryStatus;
    }


}
