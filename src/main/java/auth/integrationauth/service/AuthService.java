package auth.integrationauth.service;

import auth.integrationauth.auth.config.SecurityUtil;
import auth.integrationauth.auth.jwt.AccessTokenDto;
import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.auth.jwt.TokenProvider;
import auth.integrationauth.auth.jwt.redis.RedisService;
import auth.integrationauth.controller.dto.oauth.kakao.OauthToken;
import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.domain.Authority;
import auth.integrationauth.domain.Member;
import auth.integrationauth.domain.OauthType;
import auth.integrationauth.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Transactional
    public void signUp(SignUpDto signUpDto) {
        if(memberRepository.findByLoginId(signUpDto.getLoginId()).isPresent()){
            throw new IllegalStateException("중복된 아이디입니다.");
        }

        memberRepository.save(Member.builder()
                .loginId(signUpDto.getLoginId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .nickName(signUpDto.getNickName())
                .email(signUpDto.getEmail())
                .authority(Authority.ROLE_USER)
                .oauthType(OauthType.NORMAL)
                .build());
    }

    @Transactional
    public TokenDto signIn(SignInDto signInDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getLoginId(), signInDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        redisService.setValues(authentication.getName()
                , tokenDto.getRefreshToken()
                , Duration.ofMillis(tokenProvider.getRefreshTokenExpireTime()));

        return tokenDto;
    }


    public TokenDto refresh(String refreshToken) {

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return tokenDto;
    }


}
