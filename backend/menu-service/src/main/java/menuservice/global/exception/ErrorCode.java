package menuservice.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements DefaultErrorCode {
    // 유효성 검사 관련 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    MENU_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴 리스트를 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 메뉴를 찾을 수 없습니다."),
    INVALID_MENU_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 메뉴 타입입니다."),
    INVALID_FOLDER_ID(HttpStatus.BAD_REQUEST, "상위 메뉴가 존재하지 않거나 폴더 타입이 아닙니다."),

    FOLDER_HAS_CHILDREN_CANNOT_CHANGE(HttpStatus.CONFLICT, "하위 항목이 존재하는 경우 다른 타입(페이지/링크)으로 변경할 수 없습니다."),
    ITEM_CANNOT_CHANGE_TO_FOLDER(HttpStatus.CONFLICT, "특정 메뉴의 하위 항목인 경우 폴더 타입으로 변경할 수 없습니다."),
    DUPLICATE_PATH(HttpStatus.CONFLICT, "이미 사용중인 경로입니다.");

    private final HttpStatus status;
    private final String message;
}
