package auth.integrationauth.controller;

import auth.integrationauth.auth.config.SecurityUtil;
import auth.integrationauth.auth.jwt.AccessTokenDto;
import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.controller.dto.oauth.kakao.OauthToken;
import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.service.AuthService;
import auth.integrationauth.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OauthService oauthService;

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


        response.setHeader("Set-Cookie", setRefreshToken(tokenDto.getRefreshToken()).toString());


        return ResponseEntity.ok(new AccessTokenDto(tokenDto.getAccessToken()));
    }

    //-------------------------------------------------------------------
    @GetMapping("/oauth/token")  // <--redirect url
    public ResponseEntity kakaoSignUp(@RequestParam String code, HttpServletResponse response) {
        log.info("인가코드 = {}", code);

        OauthToken oauthToken = oauthService.getAccessToken(code);

        TokenDto tokenDto = oauthService.signIn(oauthToken.getAccess_token());

        response.setHeader("Set-Cookie", setRefreshToken(tokenDto.getRefreshToken()).toString());

        return ResponseEntity.ok(new AccessTokenDto(tokenDto.getAccessToken()));
    }
    //----------------------------------------------------------------


    @PostMapping("/refresh")
    public ResponseEntity refresh(@CookieValue(name = "RefreshToken") String refreshToken,
                                  HttpServletResponse response) {
        log.info("-------재발급 요청-----------");
        log.info("쿠키 value : {}", refreshToken);

        TokenDto tokenDto = authService.refresh(refreshToken);

        log.info("새로 발급한 쿠키 : {}", tokenDto.getRefreshToken());

        //refresh token cookie 로 전달
        response.setHeader("Set-Cookie", setRefreshToken(tokenDto.getRefreshToken()).toString());

        //access token body 로 전달
        return ResponseEntity.ok(new AccessTokenDto(tokenDto.getAccessToken()));
    }

    public ResponseCookie setRefreshToken(String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(60 * 60 * 24 * 7)  //7일
                .sameSite("None")
                .path("/")
                .build();
        return cookie;
    }

    @GetMapping("/test")
    public void test(@RequestHeader MultiValueMap<String, String> headers) {
        log.info("headers = {}", headers);
    }


}
