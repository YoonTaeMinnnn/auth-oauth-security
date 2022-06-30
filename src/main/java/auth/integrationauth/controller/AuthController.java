package auth.integrationauth.controller;

import auth.integrationauth.auth.jwt.AccessTokenDto;
import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
    }


    /**
     * @param signInDto
     * @param response
     * @return body : access token, cookie : refresh token 넣어서 응답
     */
    @PostMapping("/sign-in")
    public ResponseEntity signIn(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        TokenDto tokenDto = authService.signIn(signInDto);
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(60 * 60 * 24 * 7)  //7일
                .sameSite("None")
                .path("/")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());


        return ResponseEntity.ok(AccessTokenDto.builder().accessToken(tokenDto.getAccessToken()).build());
    }

    /**
     * 로그아웃 시, 쿠키에 있는 refresh token 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", null)
                .maxAge(0)
                .path("/")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("ok");
    }

    @PostMapping("reissue")
    public ResponseEntity reissue() {
        return ResponseEntity.ok("ok");
    }
}
