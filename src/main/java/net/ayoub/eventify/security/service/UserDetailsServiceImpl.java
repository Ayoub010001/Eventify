package net.ayoub.eventify.security.service;

import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserEntity addUser(String username, String password, String email) {
        UserEntity userCheck = userRepository.findByUserName(username).orElse(null);
        if(userCheck!=null){
            throw new RuntimeException("User already exists");
        }
        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword(passwordEncoder().encode(password));
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Override
    public Role addRole(String role) {
        Role roleCheck = rolesRepository.findByRoleName(role).orElse(null);
        if(roleCheck!=null){
            throw new RuntimeException("Role already exists");
        }
        Role roleAdded = new Role();
        roleAdded.setRoleName(role);

        return rolesRepository.save(roleAdded);
    }

    @Override
    public void addRoleToUser(String username, String role) {
        UserEntity userFound = userRepository.findByUserName(username).orElse(null);
        Role roleFound = rolesRepository.findByRoleName(role).orElse(null);
        if(userFound==null){
            throw new RuntimeException("User not found");
        }
        if(roleFound==null){
            throw new RuntimeException("Role not found");
        }
        userFound.getRoles().add(roleFound);
    }

    @Override
    public void removeRoleFromUser(String username, String role) {
        UserEntity userFound = userRepository.findByUserName(username).orElse(null);
        Role roleFound = rolesRepository.findByRoleName(role).orElse(null);
        if(userFound==null){
            throw new RuntimeException("User not found");
        }
        if(roleFound==null){
            throw new RuntimeException("Role not found");
        }
        userFound.getRoles().remove(roleFound);
    }
}
