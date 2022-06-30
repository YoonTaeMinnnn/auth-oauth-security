package auth.integrationauth.controller;

import auth.integrationauth.auth.config.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/user")
    public String info() {
        return SecurityUtil.getCurrentMemberId();
    }
}
