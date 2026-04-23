package authservice.global.exception;

import lombok.Getter;

@Getter
public class CustomAuthException extends RuntimeException {
	
	private static final long serialVersionUID = 8243575587241282402L;
	
	private final ErrorCode errorCode;

    public CustomAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomAuthException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}