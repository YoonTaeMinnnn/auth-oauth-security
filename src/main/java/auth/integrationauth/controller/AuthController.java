package auth.integrationauth.controller;

import auth.integrationauth.controller.dto.user.SignInDto;
import auth.integrationauth.controller.dto.user.SignUpDto;
import auth.integrationauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity signIn(@RequestBody SignInDto signInDto) {
        return ResponseEntity.ok(authService.signIn(signInDto));
    }

    @PostMapping("reissue")
    public ResponseEntity reissue() {
        return ResponseEntity.ok("ok");
    }
}
