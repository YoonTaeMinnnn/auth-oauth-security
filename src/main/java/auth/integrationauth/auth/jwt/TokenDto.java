package auth.integrationauth.auth.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TokenDto {

    private String accessToken;

    private String refreshToken;

}
