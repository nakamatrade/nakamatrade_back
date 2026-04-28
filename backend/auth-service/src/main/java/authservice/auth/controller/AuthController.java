package authservice.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authservice.auth.dto.LoginRequest;
import authservice.auth.dto.TokenDto;
import authservice.auth.service.AuthService;
import authservice.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@Valid @RequestBody LoginRequest request) {
        TokenDto tokenDto = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(tokenDto));
    }
}