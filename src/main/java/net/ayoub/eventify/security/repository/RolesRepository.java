package net.ayoub.eventify.security.repository;

import net.ayoub.eventify.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository  extends JpaRepository<Role, Long> {
}
