package menuservice.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;

public record MenuRequest (
		@Schema(description = "메뉴명", example = "경매")
        @NotBlank(message = "메뉴명을 입력해주세요.")
		String name,
		
		@Schema(description = "메뉴 경로", example = "/user/auction")
        @NotBlank(message = "메뉴 경로를 입력해주세요.")
		String path,
		
		@Schema(description = "사용여부, default = false", example = "true")
		boolean isUsed,
		
		@Schema(description = "메뉴 타입(Folder-최상위메뉴, Page-하위메뉴, Link-페이지 링크)", example = "FOLDER")
        @NotNull(message = "메뉴 타입을 입력해주세요.")
		MenuType type,
		
		@Schema(description = "부모 메뉴 ID, 최상위 메뉴일 경우 비워둔다")
		Long parentId,
		
		@Schema(description = "정렬 순번, 미입력시 순번 자동 증가", example = "1")
		Integer orderSn,

		@Schema(description = "메뉴를 노출할 권한", example = "USER")
        @NotNull(message = "권한을 입력해주세요.")
		MenuRole role
) {

}
