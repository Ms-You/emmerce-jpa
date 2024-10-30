package commerce.emmerce_jpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long memberId;

    private String name;

    private String email;

    private String password;

    private String tel;

    private String birth;

    private Integer point;  // 보유 포인트

    private RoleType role;

    private String city;

    private String street;

    private String zipcode;

    @Builder(builderMethodName = "createMember")
    private Member(Long id, String name, String email, String password, String tel, String birth,
                      Integer point, RoleType role, String city, String street, String zipcode) {
        this.memberId = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tel = tel;
        this.birth = birth;
        this.point = point;
        this.role = role;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
