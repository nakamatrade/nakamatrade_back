package menuservice.global.exception;

import org.springframework.http.HttpStatus;

public interface DefaultErrorCode {
    HttpStatus getStatus();
    String getMessage();
}
