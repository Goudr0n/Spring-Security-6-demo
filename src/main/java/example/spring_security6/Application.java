package example.spring_security6;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    protected CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            User user1 = User.builder()
                    .username("root")
                    .password(encoder.encode("root"))
                    .fullName("Аксёнов Евгений Андреевич")
                    .role(User.Role.ADMIN)
                    .build();

            User user2 = User.builder()
                    .username("bob")
                    .password(encoder.encode("bob"))
                    .fullName("Боб Бобинский")
                    .role(User.Role.USER)
                    .build();

            userRepository.saveAll(List.of(user1, user2));
        };
    }
}
