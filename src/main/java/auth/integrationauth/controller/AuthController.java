package auth.integrationauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/sign-up")
    public ResponseEntity siginUp() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/sign-in")
    public ResponseEntity siginIn() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("reissue")
    public ResponseEntity reissue() {
        return ResponseEntity.ok("ok");
    }
}
