package net.ayoub.eventify.security.repository;

import net.ayoub.eventify.security.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
