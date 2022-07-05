package auth.integrationauth.controller.dto.oauth.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OauthToken {

    private String token_type;

    private String access_token;

    private int expires_in;

    private String refresh_token;

    private int refresh_token_expires_in;

    private String scope;
}
