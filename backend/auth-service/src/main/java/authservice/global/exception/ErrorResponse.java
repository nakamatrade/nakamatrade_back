package authservice.global.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ErrorResponse {
    
    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;
    
    // @Valid 유효성 검사 통과되지 못한 항목 담을 Map, 유효성 검사 통과시 JSON 직렬화에서 제외
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> validation;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Validation 에러를 담을 생성자
    public ErrorResponse(String errorCode, String message, Map<String, String> validation) {
        this.errorCode = errorCode;
        this.message = message;
        this.validation = validation;
        this.timestamp = LocalDateTime.now();
    }
}