package net.ayoub.eventify.security.service;

import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.security.entities.Role;

public interface UserDetailsService {

    UserEntity addUser(String username, String password, String email);
    Role addRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
}
