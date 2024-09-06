package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByUserEntityCommentedAndEventCommented(UserEntity user, Event event);
    Collection<Comment> findAllByEventCommented(Event event);
    Comment findCommentByCommentId(Long commentId);
}
