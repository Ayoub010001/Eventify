package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.security.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByUserName(String userName);
}
