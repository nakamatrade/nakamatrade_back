package menuservice.menu.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import menuservice.menu.domain.Menu;
import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;

@JsonInclude(Include.NON_EMPTY)
public record MenuResponse(
    Long id,
    String name,
    String path,
    boolean isUsed,
    MenuType type,
    MenuRole role,
    Long parentId,
    Integer orderSn,
    List<MenuResponse> children
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
            menu.getId(),
            menu.getName(),
            menu.getPath(),
            menu.isUsed(),
            menu.getType(),
            menu.getRole(),
			menu.getParent() != null ? menu.getParent().getId() : null,
			menu.getOrderSn(),
			menu.getChildren().stream()
					.map(MenuResponse::from)
					.sorted(Comparator.comparingInt(m -> m.orderSn() != null ? m.orderSn() : 0))
					.collect(Collectors.toList())
        );
    }
}