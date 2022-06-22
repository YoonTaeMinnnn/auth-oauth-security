package auth.integrationauth.service;

import auth.integrationauth.auth.jwt.TokenDto;
import auth.integrationauth.auth.jwt.TokenProvider;
import auth.integrationauth.auth.jwt.redis.RedisService;
import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.domain.User;
import auth.integrationauth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Transactional
    public void signUp(SignUpDto signUpDto) {
        userRepository.findByLoginId(signUpDto.getLoginId()).orElseThrow(
                () -> new IllegalStateException("중복된 아이디를 사용할 수 없습니다")
        );
        userRepository.save(User.builder()
                .loginId(signUpDto.getLoginId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build());
    }

//    @Transactional
//    public TokenDto signIn(SignInDto signInDto) {
//
//    }



}
