package menuservice.global.swagger;

import menuservice.global.exception.ErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponse {
    ErrorCode[] value();

    String description() default "예외 발생 시 예외발생 시점과 에러코드와 에러메시지가 전달된다.";
}
