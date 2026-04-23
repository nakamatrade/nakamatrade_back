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
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!roleRepository.findByType(("USER")).isPresent()) {
            roleRepository.save(new Role("USER", "일반사용자"));
        }
        
        if (!roleRepository.findByType("ADMIN").isPresent()) {
            roleRepository.save(new Role("ADMIN", "시스템 관리자"));
        }
    }
}
