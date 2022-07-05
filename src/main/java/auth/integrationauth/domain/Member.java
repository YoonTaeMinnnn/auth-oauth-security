package auth.integrationauth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String loginId;

    private String password;

    private String nickName;

    private String email;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Authority authority;



    @Builder
    public Member(String loginId, String password, Authority authority) {
        this.loginId = loginId;
        this.password = password;
        this.authority = authority;
    }
}
