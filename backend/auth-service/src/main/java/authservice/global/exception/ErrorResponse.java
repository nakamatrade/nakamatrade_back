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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> validation;

    private ErrorResponse(String errorCode, String message, Map<String, String> validation) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.validation = validation != null ? Map.copyOf(validation) : Map.of();
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, null);
    }

    public static ErrorResponse of(String errorCode, String message, Map<String, String> validation) {
        return new ErrorResponse(errorCode, message, validation);
    }
}
