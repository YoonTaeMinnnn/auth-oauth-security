package auth.integrationauth.controller.dto.user;

import lombok.Data;

@Data
public class SignInDto {

    private String loginId;

    private String password;
}
