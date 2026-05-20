package menuservice.menu.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import menuservice.global.exception.BusinessException;
import menuservice.global.exception.ErrorCode;
import menuservice.menu.domain.Menu;
import menuservice.menu.domain.enums.MenuType;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.dto.MenuResponse;
import menuservice.menu.repository.MenuRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
	
    private final MenuSequenceManager sequenceManager;
    private final MenuRepository menuRepository;

    @Transactional
    public MenuResponse createMenu(MenuRequest requestDto) {
        if (pathExistsCheck(requestDto.path())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PATH);
        }

        Long parentId = requestDto.parentId();
        Integer folderSn = requestDto.folderSn();
        Integer itemSn = requestDto.itemSn();

        if (Objects.isNull(parentId)) {
        	folderSn = sequenceManager.resolveFolderSn(folderSn);
        	
        	itemSn = null;
        } else {
        	validateParentFolder(parentId, requestDto.type());
            
        	itemSn = sequenceManager.resolveItemSn(parentId, itemSn);
        	
        	folderSn = null;
        }

        Menu menu = menuRepository.save(Menu.of(requestDto, parentId, folderSn, itemSn));
        
        return MenuResponse.from(menu);
    }
    
    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest requestDto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        validatePathForUpdate(menu, requestDto.path());
        validateTypeChangeConstraint(menu, requestDto.type());

        Integer resolvedFolderSn = sequenceManager.resolveAndShiftFolderSnForUpdate(menu, requestDto);
        Integer resolvedItemSn = sequenceManager.resolveAndShiftItemSnForUpdate(menu, requestDto);

        menu.update(requestDto, resolvedFolderSn, resolvedItemSn);

        menuRepository.save(menu);

        return MenuResponse.from(menu);
    }

    public List<MenuResponse> getAllMenus() {
        List<Menu> menuAllList = menuRepository.findAll();

        return MenuResponse.assembleTree(menuAllList);
    }
    
    private boolean pathExistsCheck(String path) {
        return menuRepository.existsByPath(path);
    }
    
    private void validateParentFolder(Long parentId, MenuType menuType) {
        if (menuType == MenuType.FOLDER) {
            throw new BusinessException(ErrorCode.INVALID_MENU_TYPE);
        }

        Menu parentFolder = menuRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_FOLDER_ID));

        if (parentFolder.getType() != MenuType.FOLDER) {
            throw new BusinessException(ErrorCode.INVALID_FOLDER_ID);
        }
    }
    
    private void validatePathForUpdate(Menu menu, String newPath) {
        if (!menu.getPath().equals(newPath) && pathExistsCheck(newPath)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PATH);
        }
    }

    private void validateTypeChangeConstraint(Menu menu, MenuType newType) {
        MenuType oldType = menu.getType();
        
        if (oldType == newType) return;

        if (oldType == MenuType.FOLDER) {
            if (menuRepository.existsByParentId(menu.getId())) {
                throw new BusinessException(ErrorCode.FOLDER_HAS_CHILDREN_CANNOT_CHANGE);
            }
        } else {
        	Long parentId = menu.getParentId();
        	
            if (Objects.nonNull(parentId) && newType == MenuType.FOLDER) {
                throw new BusinessException(ErrorCode.ITEM_CANNOT_CHANGE_TO_FOLDER);
            }
        }
    }
}