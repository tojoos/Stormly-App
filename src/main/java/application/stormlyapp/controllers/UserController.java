package application.stormlyapp.controllers;

import application.stormlyapp.model.User;
import application.stormlyapp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
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
    public String processLoginForm(@Valid @ModelAttribute("user") User user, BindingResult result) {
        //todo user password and login authorization
        if (result.hasErrors()) {
            result.getAllErrors().forEach(err -> log.debug(err.toString()));
            return "user/loginForm";
        } else {
            return "redirect:/control-panel";
        }
    }

    @GetMapping("/user/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/registerForm";
    }

    @PostMapping("/user/register")
    public String processRegisterForm(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            result.getAllErrors().forEach(err -> log.debug(err.toString()));
            return "user/registerForm";
        } else {
            User newUser = userService.save(user);
            redirectAttributes.addFlashAttribute("registered", true);
            redirectAttributes.addFlashAttribute("registeredEmail", newUser.getEmail());
            return "redirect:/user/login";
        }
    }
}
