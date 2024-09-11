package net.ayoub.eventify.web;

import jakarta.validation.Valid;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.entities.UserAccount;
import net.ayoub.eventify.security.repository.RolesRepository;

import net.ayoub.eventify.security.service.UserDetailsService;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public AuthController(PasswordEncoder passwordEncoder, RolesRepository rolesRepository, UserRepository userRepository, UserDetailsService userDetailsService) {
        //this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/auth")
    public String inscription(Model model) {
        UserEntity user = new UserEntity();
        model.addAttribute("userAccount", user);
        return "signUpForm";
    }

    @PostMapping("/auth/register")
    public String register(Model model,
                           @Valid @ModelAttribute("userAccount") UserEntity userAccount, BindingResult result) {

        if (result.hasErrors()){
            return "signUpForm";
        }
        // Check if username already exists
        if (userRepository.existsByUserName(userAccount.getUserName())) {
            result.rejectValue("userName", "error.user", "Username already exists");
            return "signUpForm"; // Return back to the registration form with error
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userAccount.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "signUpForm"; // Return back to the registration form with error
        }
        userDetailsService.addUser(userAccount.getUserName(),userAccount.getPassword(), userAccount.getEmail());
        userDetailsService.addRoleToUser(userAccount.getUserName(),"ROLE_USER");
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String login() {
        //return login.html located in /resources/templates
        return "login";
    }
    @GetMapping(value = "/notAuthorized")
    public String accessDenied(){
        return "notAuthorized";
    }
}
