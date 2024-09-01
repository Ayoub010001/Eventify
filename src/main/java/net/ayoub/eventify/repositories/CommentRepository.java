package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
