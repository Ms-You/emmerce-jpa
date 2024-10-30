package commerce.emmerce_jpa.controller;

import commerce.emmerce_jpa.dto.MemberDTO;
import commerce.emmerce_jpa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity signup(@RequestBody MemberDTO.RegisterReq registerReq) {
        authService.register(registerReq);

        return new ResponseEntity("회원가입 완료", HttpStatus.OK);
    }


    @PostMapping("/duplicate-check")
    public ResponseEntity duplicateCheckName(@RequestBody MemberDTO.DuplicateCheckReq duplicateCheckReq) {
        authService.duplicateCheck(duplicateCheckReq);

        return new ResponseEntity("사용 가능", HttpStatus.OK);
    }


    @PostMapping("/login")
    public Mono<HttpHeaders> login(@RequestBody MemberDTO.LoginReq loginReq) {
        return authService.login(loginReq)
                .map(tokenDTO -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + tokenDTO.getAccessToken());
                    httpHeaders.add("RefreshToken", tokenDTO.getRefreshToken());

                    return httpHeaders;
                });
    }
}
