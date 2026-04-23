package authservice.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	
	// 계정 및 권한 관련 에러
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "권한이 존재하지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "계정이 잠겼습니다. 비밀번호 찾기를 진행해주세요."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    
    // 유효성 검사 관련 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    
    // JWT 관련 에러
    INVALID_TOKEN_AUTHORITY(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다.");

    private final HttpStatus status;
    private final String message;
}
