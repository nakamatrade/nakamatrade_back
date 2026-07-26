package menuservice.global.utils;

import lombok.RequiredArgsConstructor;
import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.dto.MenuResponse;
import menuservice.menu.repository.MenuRepository;
import menuservice.menu.service.MenuService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuInitializer implements ApplicationRunner {
    private final MenuService menuService;
    private final MenuRepository menuRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(menuRepository.count() == 0) {
            MenuRequest systemAdminMenuRequest = new MenuRequest(
                    "시스템 관리",
                    "/admin/system",
                    true,
                    MenuType.FOLDER,
                    null,
                    null,
                    MenuRole.ADMIN
            );

            MenuResponse parentResponse = menuService.createMenu(systemAdminMenuRequest);
            Long parentId = parentResponse.id();

            MenuRequest userMenuRequest = new MenuRequest(
                    "사용자 관리",
                    "/admin/system/users",
                    true,
                    MenuType.PAGE,
                    parentId,
                    null,
                    MenuRole.ADMIN
            );
            menuService.createMenu(userMenuRequest);

            MenuRequest roleMenuRequest = new MenuRequest(
                    "권한 관리",
                    "/admin/system/roles",
                    true,
                    MenuType.PAGE,
                    parentId,
                    null,
                    MenuRole.ADMIN
            );

            menuService.createMenu(roleMenuRequest);
        }
    }
}
