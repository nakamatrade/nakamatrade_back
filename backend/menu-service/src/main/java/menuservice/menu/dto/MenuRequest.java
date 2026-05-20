package menuservice.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;

public record MenuRequest (
		@Schema(description = "메뉴명", example = "경매시작하기")
        @NotBlank(message = "메뉴명을 입력해주세요.")
		String name,
		
		@Schema(description = "메뉴 경로", example = "auction")
        @NotBlank(message = "메뉴 경로를 입력해주세요.")
		String path,
		
		@Schema(description = "사용여부, default = false", example = "false")
		boolean isUsed,
		
		@Schema(description = "메뉴 타입(Folder-최상위메뉴, Page-하위메뉴, Link-페이지 링크)", example = "Folder")
        @NotNull(message = "메뉴 타입을 입력해주세요.")
		MenuType type,
		
		@Schema(description = "부모 메뉴 ID")
		Long parentId,
		
		@Schema(description = "최상위 메뉴 순번", example = "1")
		Integer folderSn,
		
		@Schema(description = "상위메뉴 하위에 위치할 경우 메뉴 순번", example = "1")
		Integer itemSn,
		
		@Schema(description = "메뉴를 노출할 권한", example = "USER")
        @NotNull(message = "권한을 입력해주세요.")
		MenuRole role
) {

}
