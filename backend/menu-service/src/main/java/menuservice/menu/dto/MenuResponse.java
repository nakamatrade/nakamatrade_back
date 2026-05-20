package menuservice.menu.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    Integer folderSn,
    Integer itemSn,
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
            menu.getParentId(),
            menu.getFolderSn(),
            menu.getItemSn(),
            new ArrayList<>()
        );
    }
    
    public static List<MenuResponse> assembleTree(List<Menu> menuList){
    	Map<Long, MenuResponse> menuMap = menuList.stream()
    			.map(MenuResponse::from)
    			.collect(Collectors.toMap(MenuResponse::id, response -> response));
    	
    	List<MenuResponse> sortedMenuList = new ArrayList<>();
    	
    	for (MenuResponse menu : menuMap.values()) {
    		if (Objects.isNull(menu.parentId())) {
    			sortedMenuList.add(menu);
    		} else {
    			MenuResponse parent = menuMap.get(menu.parentId());
    			
    			if(Objects.nonNull(parent)) {
    				parent.children().add(menu);
    			}
    		}
    	}
    	
    	sortedMenuList.sort(Comparator.comparing(MenuResponse::folderSn));
    	sortedMenuList.forEach(menu -> menu.children()
    										.sort(Comparator.comparing(MenuResponse::itemSn)));
        
        return sortedMenuList;
    }
}