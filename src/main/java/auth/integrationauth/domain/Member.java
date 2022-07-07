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
    @Column(name = "member_id")
    private Long id;

    private String loginId;  //일반 : loginId , 카카오 : kakao id(code)

    private String password;

    private String nickName;

    private String email;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private OauthType oauthType;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String accessToken;



    @Builder
    public Member(String loginId, String password, Authority authority, String nickName, String email, String imageUrl, OauthType oauthType,String accessToken) {
        this.loginId = loginId;
        this.password = password;
        this.authority = authority;
        this.nickName = nickName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.oauthType = oauthType;
        this.accessToken = accessToken;
    }

    public void updateAccessToken() {
        this.accessToken = null;
    }
}
