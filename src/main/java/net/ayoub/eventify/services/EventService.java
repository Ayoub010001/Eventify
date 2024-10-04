package net.ayoub.eventify.services;

import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    //Events
    List<Event> getAllEvents();
    Event getEventById(Long eventId);
    void deleteEvent(Long eventId) throws IOException;
    void addEvent(MultipartFile eventImage, Event event) throws IOException;
    void updateEvent(MultipartFile eventImage, Event event, Event existingEvent) throws IOException;
    //Users
    UserEntity getUserByUsername(String username);
    UserEntity getUserById(Long userId);
    //Interactions : Likes, Saves, Comments
    List<Comment> getCommentsByEvent(Event event);
    boolean isEventLikedByUser(Event event, UserEntity user);
    boolean isEventSavedByUser(Event event, UserEntity user);
    void likeEvent(Event event, UserEntity user);
    void saveEvent(Event event, UserEntity user);
    List<Event> getSavedEvents(UserEntity user);
    List<Event> getLikedEvents(UserEntity user);
    void unlikeEvent(Event event, UserEntity user);
    void unSaveEvent(Event event, UserEntity user);
    void addComment(Comment comment, UserEntity user, Event event);
    void deleteComment(Long commentId, Long userId, Long eventId);
}
