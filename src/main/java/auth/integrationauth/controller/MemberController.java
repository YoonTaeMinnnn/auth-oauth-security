package auth.integrationauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/memberInfo")
    public String info() {
        return "info";
    }
}
