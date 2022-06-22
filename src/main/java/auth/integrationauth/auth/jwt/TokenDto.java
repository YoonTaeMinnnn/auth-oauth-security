package auth.integrationauth.auth.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TokenDto {

    private String grantType;

    private String accessToken;

    private Date accessTokenExpiresIn;

    private String refreshToken;
}
