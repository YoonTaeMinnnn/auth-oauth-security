package auth.integrationauth.service;


import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.controller.dto.oauth.kakao.KakaoProfile;
import auth.integrationauth.controller.dto.oauth.kakao.OauthToken;
import auth.integrationauth.domain.Member;
import auth.integrationauth.repository.user.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final MemberRepository memberRepository;

    @Value("${kakao.clientId}")
    String client_id;

    /**
     *
     * @param 인가코드
     * @return kakao accesstoken
     */
    public OauthToken getAccessToken(String code) {
        log.info("getAccessToken 호출");
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id", client_id );
        params.add("redirect_uri", "http://localhost:8080/auth/oauth/token");   //입력
        params.add("code", code);
        //params.add("client_secret", null);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        log.info("토큰 응답값 = {}", accessTokenResponse.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;

    }

    public void signIn(String token) {
        KakaoProfile kakaoProfile = findProfile(token);
    }

    public KakaoProfile findProfile(String token) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        //------------------------------------------
        log.info("response 값 = {}", kakaoProfileResponse.getBody());
        //--------------------------------------------

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }
}

