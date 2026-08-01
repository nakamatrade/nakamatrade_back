package menuservice.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "메뉴 등록",
            description = """
                            시스템에 새로운 메뉴를 등록한다.
                          
                            ### 메뉴 타입(Type) 정의 및 역할
                            1. **FOLDER (폴더)**
                                - 최상위 메뉴에만 존재할 수 있다.
                                - 하위 메뉴를 묶어주는 그룹 역할
                                - 하위 메뉴를 가질 수 있다.
                            2. **PAGE (페이지)**
                                - 시스템 내부의 실제 화면으로 이동하는 기능을 수행
                                - 단말(Leaf) 노드로 동작하며 하위 메뉴를 가질 수 없다.
                            3. **LINK (외부 링크)**
                                - 외부 URL로 이동하는 기능 수행
                                - 단말(Leaf) 노드로 동작하며 하위 메뉴를 가질 수 없다.
                    
                            ### 메뉴 등록 비즈니스 규칙
                            1. **최상위 메뉴 등록 (parentId 미존재 시)**
                                - 허용 type: `FOLDER`, `PAGE`, `LINK`
                            2. **하위 메뉴 등록(parentId 존재 시)**
                                - 허용 type: `PAGE`, `LINK`
                            3. **정렬 순서 (orderSn)**
                                - 값을 기입하면 해당 순서로 삽입된다.
                                - 값을 생략(null)하면 해당 뎁스(Depth)의 **마지막 순서 + 1**로 자동 배정된다.
                          """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                                  메뉴 등록 요청 페이로드<br>
                                  
                                  - **메뉴(type) 종류**
                                    - FOLDER
                                    - PAGE
                                    - LINK
                                  - **권한(role) 종류**
                                    - USER
                                    - ADMIN
                                  """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "1. 최상위 폴더 등록",
                                            summary = "최상위 폴더 등록",
                                            description = """
                                                            최상위 메뉴로 폴더를 생성한다. 
                                                            순서(orderSn) 미입력시 맨 끝으로 자동 지정된다.
                                                          """,
                                            value = """
                                                    {
                                                        "name": "시스템설정",
                                                        "type": "FOLDER",
                                                        "path": "/system",
                                                        "role": "ADMIN"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "2. 하위 메뉴 등록",
                                            summary = "특정 메뉴 하위의 메뉴 등록",
                                            description = "parentId를 지정하여 하위 메뉴를 등록한다.",
                                            value = """
                                                    {
                                                        "parentId": 1,
                                                        "name": "사용자 권한 관리",
                                                        "type": "PAGE",
                                                        "path": "/system/roles",
                                                        "role": "ADMIN"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "3. 특정 순서를 지정한 메뉴 등록",
                                            summary = "정렬 순서 직접 지정",
                                            description = "orderSn을 직접 지정하여 원하는 위치에 메뉴를 삽입한다.",
                                            value = """
                                                    {
                                                        "name": "외부 링크",
                                                        "type": "LINK",
                                                        "path": "https://www.google.com",
                                                        "role": "USER",
                                                        "orderSn": 2
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
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

    @Operation(
            summary = "메뉴 Tree 조회",
            description = """
                            시스템에 등록된 전체 메뉴 목록을 계층형(Tree) 구조로 조회한다.
                            
                            ### 조회 규칙 및 특징
                            1. **계층 구조 반환**
                                - 최상위 루트 노드부터 단말 노드까지 하위객체(`children`)를 포함하는 중첩 배열 형태로 반환된다.
                            2. **정렬 기준**
                                - 각 뎁스(Depth) 내의 하위 메뉴들은 `orderSn` 값을 기준으로 오름차순 정렬되어 노출된다.
                          """
    )
    @ApiResponse(
            responseCode = "200",
            description = "시스템에 등록된 전체 메뉴 목록을 계층형(Tree) 구조로 조회"
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