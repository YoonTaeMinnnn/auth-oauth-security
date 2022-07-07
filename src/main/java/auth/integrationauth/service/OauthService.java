package auth.integrationauth.service;


import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.auth.jwt.TokenProvider;
import auth.integrationauth.controller.dto.oauth.kakao.KakaoLogoutDto;
import auth.integrationauth.controller.dto.oauth.kakao.KakaoProfile;
import auth.integrationauth.controller.dto.oauth.kakao.OauthToken;
import auth.integrationauth.domain.Authority;
import auth.integrationauth.domain.Member;
import auth.integrationauth.domain.OauthType;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.clientId}")
    String client_id;

    /**
     *
     * @param
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

    /**
     * (1) member(loginId) : kakao id , member(email) : kakao email save
     * (2) authentication ==> (kakao id , kakao id + kakao email)
     * @param token
     * @return
     */
    @Transactional
    public TokenDto signIn(String token) {
        KakaoProfile kakaoProfile = findProfile(token);

        Optional<Member> findMember = memberRepository.findByLoginId(String.valueOf(kakaoProfile.getId()));

        if (findMember.isEmpty()) {
            memberRepository.save(Member.builder()
                    .loginId(String.valueOf(kakaoProfile.getId()))
                    .password(passwordEncoder.encode(String.valueOf(kakaoProfile.getId()) + kakaoProfile.getKakao_account().getEmail()))
                    .email(kakaoProfile.getKakao_account().getEmail())
                    .authority(Authority.ROLE_USER)
                    .nickName(kakaoProfile.getKakao_account().getProfile().getNickname())
                    .imageUrl(kakaoProfile.getKakao_account().getProfile().getProfile_image_url())
                    .oauthType(OauthType.KAKAO)
                    .accessToken(token)
                    .build());
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                String.valueOf(kakaoProfile.getId()), String.valueOf(kakaoProfile.getId()) + kakaoProfile.getKakao_account().getEmail()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return tokenDto;

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

    @Transactional
    public void logout(Member member) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + member.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoLogoutResponse = rt.exchange(
                "https://kapi.kakao.com/v1/user/logout",
                HttpMethod.POST,
                kakaoLogoutRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoLogoutDto kakaoLogoutDto = null;

        try {
            kakaoLogoutDto = objectMapper.readValue(kakaoLogoutResponse.getBody(), KakaoLogoutDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        member.updateAccessToken();
    }
}

