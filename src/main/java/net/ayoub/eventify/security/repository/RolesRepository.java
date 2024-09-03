package net.ayoub.eventify.security.repository;

import net.ayoub.eventify.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
