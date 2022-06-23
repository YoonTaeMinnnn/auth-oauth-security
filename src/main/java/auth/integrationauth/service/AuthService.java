package auth.integrationauth.service;

import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.auth.jwt.TokenProvider;
import auth.integrationauth.auth.jwt.redis.RedisService;
import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.domain.Authority;
import auth.integrationauth.domain.Member;
import auth.integrationauth.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Transactional
    public void signUp(SignUpDto signUpDto) {
        if(userRepository.findByLoginId(signUpDto.getLoginId()).isPresent()){
            throw new IllegalStateException("중복된 회원입니다");
        }

        userRepository.save(Member.builder()
                .loginId(signUpDto.getLoginId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .authority(Authority.ROLE_USER)
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



}
