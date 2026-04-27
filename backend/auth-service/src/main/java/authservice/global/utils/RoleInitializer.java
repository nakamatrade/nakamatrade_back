package authservice.global.utils;

import authservice.user.domain.Role;
import authservice.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private static final String ROLE_USER_TYPE = "USER";
    private static final String ROLE_ADMIN_TYPE = "ADMIN";
    private static final String ROLE_USER_DESCRIPTION = "일반사용자";
    private static final String ROLE_ADMIN_DESCRIPTION = "시스템 관리자";

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        initializeRole(ROLE_USER_TYPE, ROLE_USER_DESCRIPTION);
        initializeRole(ROLE_ADMIN_TYPE, ROLE_ADMIN_DESCRIPTION);
    }

    private void initializeRole(String type, String description) {
        if (roleRepository.findByType(type).isEmpty()) {
            roleRepository.save(new Role(type, description));
        }
    }
}
