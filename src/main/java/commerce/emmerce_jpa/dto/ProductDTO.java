package commerce.emmerce_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProductDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductReq {
        private String name;
        private String detail;
        private Integer originalPrice;
        private Integer discountPrice;
        private Integer stockQuantity;
        private String brand;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateReq {
        private String name;
        private String detail;
        private Integer originalPrice;
        private Integer discountPrice;
        private Integer stockQuantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResp {
        private Long productId;
        private String name;
        private String detail;
        private Integer originalPrice;
        private Integer discountPrice;
        private Integer discountRate;
        private Integer stockQuantity;
        private Double starScore;
        private Integer totalReviews;
        private String titleImg;
        private List<String> detailImgList;
        private String brand;
        private LocalDateTime enrollTime;
        private Long likeCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResp {
        private Long productId;
        private String name;
        private Integer originalPrice;
        private Integer discountPrice;
        private Integer discountRate;
        private Double starScore;
        private Integer totalReviews;
        private String titleImg;
        private Long likeCount;
        private String brand;
    }

}
