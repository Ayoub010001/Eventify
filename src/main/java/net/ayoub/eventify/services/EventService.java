package net.ayoub.eventify.services;

import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    List<Event> getAllEvents();
    Event getEventById(Long eventId);
    Event deleteEvent(Long eventId) throws IOException;
    Event addEvent(MultipartFile eventImage,Event event) throws IOException;
    Event updateEvent(MultipartFile eventImage,Event event) throws IOException;

    UserEntity getUserByUsername(String username);
    UserEntity getUserById(Long userId);

    List<Comment> getCommentsByEvent(Event event);

    boolean isEventLikedByUser(Event event, UserEntity user);
    boolean isEventSavedByUser(Event event, UserEntity user);
}
