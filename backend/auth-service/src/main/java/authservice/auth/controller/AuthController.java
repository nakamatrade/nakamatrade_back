package authservice.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authservice.auth.dto.LoginRequest;
import authservice.auth.dto.SignupRequest;
import authservice.auth.dto.TokenDto;
import authservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest request) {
        TokenDto tokenDto = authService.login(request);
        
        return ResponseEntity.ok(tokenDto);
    }
    
    @PostMapping("/exists")
    public ResponseEntity<Boolean> usernameExists(@RequestBody SignupRequest request){
		return ResponseEntity.ok(authService.usernameExistsCheck(request));
    }
}