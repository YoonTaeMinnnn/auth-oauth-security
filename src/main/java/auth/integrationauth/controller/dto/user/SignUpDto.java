package auth.integrationauth.controller.dto.user;

import lombok.Data;

@Data
public class SignUpDto {

    private String loginId;

    private String password;
}
