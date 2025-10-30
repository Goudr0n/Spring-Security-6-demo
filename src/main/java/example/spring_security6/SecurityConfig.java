package example.spring_security6;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class SecurityConfig {

    // Обязательный бин для шифрования паролей, необходимый при аутентификации
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Обязательный бин для загрузки пользователя (из БД в нашем случае)
    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return username -> {
            Optional<User> user = repository.findByUsername(username);
            return user.orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
        };
    }

    // Обязательный бин правил авторизации
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/main").hasAnyRole("USER", "ADMIN") // Префикс "ROLE_" не нужен
                        .anyRequest().permitAll()) // Разрешить доступ к странице логина и т.п. Порядок разрешений важен
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // для iframe
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // отключить CSRF для консоли H2
                .formLogin(form -> form
                        .loginPage("/customLogin") // URL контроллера входа
                        // Любой URL, на который отправляется POST с формой (action должен соответствовать)
                        // Свой контроллер не нужен и не рекомендуется - Spring сделает всё сам
                        .loginProcessingUrl("/authenticate")
                        // Имя поля для логина в форме; "username" по умолчанию
                        .usernameParameter("user")
                        // Имя поля для пароля в форме; "password" по умолчанию
                        .passwordParameter("pass")
                        // Логика при успешном входе
                        .successHandler((request, response, authentication) -> {
                            User user = (User) authentication.getPrincipal();
                            request.getSession().setAttribute("user", user);
                        })
                        // true - принудительно использовать этот URL, если пользователь запрашивал другой защищённый ресурс
                        // "/" по умолчанию
                        .defaultSuccessUrl("/main", true)
                ).logout(logout -> logout
                        .logoutUrl("/logout") // URL, на который отправляется POST-запрос
                        .logoutSuccessUrl("/customLogin?logout") // куда перенаправить после выхода
                        .invalidateHttpSession(true) // удалить сессию
                        .deleteCookies("JSESSIONID") // удалить куки
                        .permitAll()).build();
    }

}
