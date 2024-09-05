package net.ayoub.eventify.web;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    //private final UserAccountRepository userAccountRepository;
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
    public String register(Model model, UserEntity user) {
        if(userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("User name already exists");
        }
        userDetailsService.addUser(user.getUserName(),user.getPassword(), user.getEmail());
        userDetailsService.addRoleToUser(user.getUserName(),"ROLE_USER");

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
