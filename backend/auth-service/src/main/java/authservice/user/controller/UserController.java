package authservice.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import authservice.global.common.ApiResponse;
import authservice.user.dto.SignupRequest;
import authservice.user.dto.SignupResponse;
import authservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final String SIGNUP_SUCCESS_MESSAGE = "회원가입이 완료되었습니다.";
	
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
    	SignupResponse signupResponse = userService.signup(request);

        return ResponseEntity.ok(ApiResponse.success(SIGNUP_SUCCESS_MESSAGE, signupResponse));
    }
    
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> usernameExists(@RequestParam String username) {
    	Boolean existsChecked = Boolean.valueOf(userService.usernameExistsCheck(username));
    	
        return ResponseEntity.ok(ApiResponse.success(existsChecked));
    }
}
