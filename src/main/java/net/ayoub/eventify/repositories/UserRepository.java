package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
