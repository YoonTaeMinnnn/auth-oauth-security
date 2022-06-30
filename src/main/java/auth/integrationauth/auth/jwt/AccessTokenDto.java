package auth.integrationauth.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenDto {

    private String accessToken;
}
