package menuservice.menu.service;

import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import menuservice.menu.domain.Menu;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.repository.MenuRepository;

@Component
@RequiredArgsConstructor
public class MenuSequenceManager {
	private final MenuRepository menuRepository;
	
    public int resolveFolderSn(Integer folderSn) {
        int currentMaxSn = menuRepository.findMaxFolderSnByParentIdIsNull().orElse(0);
        
        int targetSn;
        
        if (Objects.isNull(folderSn) || folderSn > currentMaxSn + 1) {
            targetSn = currentMaxSn + 1;
        } else {
            targetSn = folderSn;
        }

        if (targetSn <= currentMaxSn) {
            menuRepository.shiftUpFolderSnAfter(targetSn);
        }
        
        return targetSn;
    }

    public int resolveItemSn(Long parentId, Integer itemSn) {
    	int currentMaxSn = menuRepository.findMaxItemSnByParentId(parentId).orElse(0);
        
    	int targetSn;
    	
        if (Objects.isNull(itemSn) || itemSn > currentMaxSn + 1) {
            targetSn = currentMaxSn + 1;
        } else {
            targetSn = itemSn;
        }

        if (targetSn <= currentMaxSn) {
            menuRepository.shiftUpItemSnAfter(parentId, targetSn);
        }
        
        return targetSn;
    }

    public Integer resolveAndShiftFolderSnForUpdate(Menu menu, MenuRequest requestDto) {
        Long parentId = requestDto.parentId();
        
    	if (Objects.nonNull(parentId)) return null;

        Integer oldFolderSn = menu.getFolderSn();
        Integer newFolderSn = requestDto.folderSn();

        if (Objects.nonNull(newFolderSn)) {
            if (Objects.isNull(oldFolderSn)) {
            	/*
            	 * 1. 하위 메뉴에서 최상위 메뉴로 계층 이동한 경우
            	 */
                menuRepository.shiftUpFolderSnAfter(newFolderSn);
            } else if (!newFolderSn.equals(oldFolderSn)) {
                /*
                 * 1. 기존에 최상위 메뉴였고, 순서만 변경하는 경우
                 * */
                if (newFolderSn < oldFolderSn) {
                    menuRepository.shiftUpFolderSnBetween(newFolderSn, oldFolderSn);
                } else {
                    menuRepository.shiftDownFolderSnBetween(oldFolderSn, newFolderSn);
                }
            }
        }
        
        return Objects.nonNull(newFolderSn) ? newFolderSn : oldFolderSn;
    }

    public Integer resolveAndShiftItemSnForUpdate(Menu menu, MenuRequest requestDto) {
        Long parentId = requestDto.parentId();
        
        if (Objects.isNull(parentId)) return null;

        Integer oldItemSn = menu.getItemSn();
        Integer newItemSn = requestDto.itemSn();
        Long oldParentId = menu.getParentId();

        if (Objects.nonNull(newItemSn)) { 
            if (Objects.isNull(oldItemSn) || !parentId.equals(oldParentId)) {
            	/*
            	 * 1. 기존 최상위 메뉴에서 하위 항목으로 이동한 경우
            	 * 2. 다른 최상위 메뉴 하위 항목으로 이동한 경우
            	 */
                menuRepository.shiftUpItemSnAfter(parentId, newItemSn);
            } else if (!newItemSn.equals(oldItemSn)) {
                /*
                 * 1. 기존 최상위 메뉴 하위 항목인 상태에서 순서만 변경하는 경우
                 * */
                if (newItemSn < oldItemSn) {
                    menuRepository.shiftUpItemSnBetween(parentId, newItemSn, oldItemSn);
                } else {
                    menuRepository.shiftDownItemSnBetween(parentId, oldItemSn, newItemSn);
                }
            }
        }
        
        return Objects.nonNull(newItemSn) ? newItemSn : oldItemSn;
    }
}
