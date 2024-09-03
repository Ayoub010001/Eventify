package net.ayoub.eventify.web;

import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.entities.UserAccount;
import net.ayoub.eventify.security.repository.RolesRepository;
import net.ayoub.eventify.security.repository.UserAccountRepository;
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

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    public AuthController(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    @GetMapping("/auth")
    public String inscription(Model model) {
        UserAccount user = new UserAccount();
        model.addAttribute("userAccount", user);
        return "signUpForm";
    }

    @PostMapping("/auth/register")
    public String register(Model model, UserAccount user) {
        if(userAccountRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("User name already exists");
        }

        UserAccount useraccount = new UserAccount();
        useraccount.setUserName(user.getUserName());
        useraccount.setPassword(passwordEncoder.encode(user.getPassword()));
        useraccount.setEmail(user.getEmail());

        Role roles = rolesRepository.findByRoleName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        System.out.println(roles.getRoleName());
        user.setRoles(Collections.singletonList(roles));
        userAccountRepository.save(useraccount);
        return "redirect:/";
    }
}
