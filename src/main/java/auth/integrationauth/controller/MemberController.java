package auth.integrationauth.controller;

import auth.integrationauth.auth.config.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class MemberController {

    @GetMapping("/user")
    public String info() {
        return SecurityUtil.getCurrentMemberId();
    }

    /**
     * 로그아웃 시, 쿠키에 있는 refresh token 삭제
     */
    @PostMapping("/log-out")
    public ResponseEntity logout(HttpServletResponse response, @CookieValue(name = "RefreshToken") String refreshToken) {
        log.info("=========로그아웃 호출=========");
        log.info("refreshtoken = {}", refreshToken);
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", null)
                .maxAge(0)
                .path("/")
                .sameSite("None")  //빌드 후 배포 시엔 수정 예정
                .secure(true)
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("ok");
    }


}
