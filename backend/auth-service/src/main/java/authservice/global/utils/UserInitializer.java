package authservice.global.utils;

import authservice.role.domain.Role;
import authservice.role.service.RoleService;
import authservice.user.domain.User;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {
    private final RoleService roleService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role initialRole = roleService.getInitialRoleForSignup();

        User kmg = User.builder()
                .username("kmg99")
                .password(passwordEncoder.encode("kmg123"))
                .email("email1@email.com")
                .nickName("kmg")
                .birthDay("19990101")
                .role(initialRole)
                .build();

        User thy = User.builder()
                .username("thy97")
                .password(passwordEncoder.encode("thy123"))
                .email("email2@email.com")
                .nickName("thy")
                .birthDay("19970101")
                .role(initialRole)
                .build();

        User ljh = User.builder()
                .username("ljh99")
                .password(passwordEncoder.encode("ljh123"))
                .email("email3@email.com")
                .nickName("ljh")
                .birthDay("19990101")
                .role(initialRole)
                .build();

        if(!userRepository.findByUsername(kmg.getUsername()).isPresent()){
            userRepository.save(kmg);
        }

        if(!userRepository.findByUsername(thy.getUsername()).isPresent()){
            userRepository.save(thy);
        }

        if(!userRepository.findByUsername(ljh.getUsername()).isPresent()){
            userRepository.save(ljh);
        }
    }
}
