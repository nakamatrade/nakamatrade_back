package menuservice.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import menuservice.global.common.Result;
import menuservice.global.exception.ErrorCode;
import menuservice.global.swagger.ApiErrorResponse;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.dto.MenuResponse;
import menuservice.menu.service.MenuService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "메뉴 관리", description = "메뉴 관련 도메인 서비스")
@RestController
@RequestMapping("/api/menu") 
@RequiredArgsConstructor
public class MenuController {
    
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final MenuService menuService;

    @Operation(summary = "메뉴 등록")
    @ApiResponse(
            responseCode = "200",
            description = "메뉴시 응답메시지와 메뉴 정보가 반환된다."
    )
    @ApiErrorResponse(value = {
            ErrorCode.INVALID_INPUT_VALUE,
            ErrorCode.DUPLICATE_PATH,
            ErrorCode.INVALID_FOLDER_ID
    })
    @PostMapping(produces = CONTENT_TYPE_JSON)
    public Result<MenuResponse> createMenu(@Valid @RequestBody MenuRequest requestDto) {
        MenuResponse response = menuService.createMenu(requestDto);

        return Result.success(response);
    }

    @Operation(
            summary = "메뉴 수정",
            parameters = {
                    @Parameter(
                            name = "menuId",
                            description = "메뉴 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공시 응답메시지와 메뉴 정보가 반환된다."
    )
    @ApiErrorResponse(value = {
            ErrorCode.MENU_NOT_FOUND,
            ErrorCode.DUPLICATE_PATH,
            ErrorCode.FOLDER_HAS_CHILDREN_CANNOT_CHANGE,
            ErrorCode.ITEM_CANNOT_CHANGE_TO_FOLDER
    })
    @PutMapping(value = "/{menuId}", produces = CONTENT_TYPE_JSON)
    public Result<Object> updateMenu(@PathVariable("menuId") Long menuId, @RequestBody MenuRequest request) {
        menuService.updateMenu(menuId, request);

        return Result.noContent();
    }

    @Operation(summary = "메뉴 Tree 조회")
    @ApiResponse(
            responseCode = "200",
            description = "요청시 메뉴가 Tree 구조로 반환된다."
    )
    @ApiErrorResponse(value = {
            ErrorCode.MENU_LIST_NOT_FOUND
    })
    @GetMapping(produces = CONTENT_TYPE_JSON)
    public Result<List<MenuResponse>> getAllMenus() {
        List<MenuResponse> response = menuService.getAllMenus();

        return Result.success(response);
    }
}