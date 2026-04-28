package authservice.global.utils;

import authservice.role.domain.Role;
import authservice.role.domain.RoleType;
import authservice.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!roleRepository.findByType(RoleType.USER).isPresent()) {
            roleRepository.save(new Role(RoleType.USER));
        }
        
        if (!roleRepository.findByType(RoleType.ADMIN).isPresent()) {
            roleRepository.save(new Role(RoleType.ADMIN));
        }
    }
}
