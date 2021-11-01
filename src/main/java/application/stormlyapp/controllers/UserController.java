package application.stormlyapp.controllers;

import application.stormlyapp.model.User;
import application.stormlyapp.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "user/loginForm";
    }

    @PostMapping("/user/login")
    public String processLoginForm(@ModelAttribute("user") User user) {
        //todo
        if (true) {
            return "redirect:/control-panel";
        } else {
            return "user/loginForm";
        }
    }

    @GetMapping("/user/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/registerForm";
    }

    @PostMapping("/user/register")
    public String processRegisterForm(@ModelAttribute("user") User user) {
        //todo
        if(true) {
            return "redirect:/user/login";
        } else {
            return "user/registerForm";
        }
    }
}
