package example.spring_security6;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {

    @ModelAttribute("user")
    public User exposeUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @GetMapping
    public String showMain() {
        return "main";
    }
}
