package authservice.global.utils;

import authservice.user.domain.User;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User kmg = User.builder()
                .username("kmg99")
                .password(passwordEncoder.encode("kmg123"))
                .birthDay("19990101")
                .nickName("kmg")
                .email("email1@email.com")
                .build();

        User thy = User.builder()
                .username("thy97")
                .password(passwordEncoder.encode("thy123"))
                .birthDay("19970101")
                .nickName("thy")
                .email("email2@email.com")
                .build();

        User ljh = User.builder()
                .username("ljh99")
                .password(passwordEncoder.encode("ljh123"))
                .birthDay("19990101")
                .nickName("ljh")
                .email("email3@email.com")
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
