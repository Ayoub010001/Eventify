package net.ayoub.eventify.services;

import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.CommentRepository;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService{

    private final String UPLOAD_DIR = "src/main/resources/uploads";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    //Events

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    @Override
    public void deleteEvent(Long eventId) throws IOException {
        //Find event
        Event event = getEventById(eventId);
        //delete Image if exit
        if(event.getEventImage() != null){
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            Path filePath = uploadDirPath.resolve(event.getEventImage());

            if(Files.exists(filePath)){
                Files.delete(filePath);
                System.out.println("File deleted: " + filePath);
            }else {
                System.out.println("File does not exist: " + filePath);
            }
        }
        //event delete likes and saves
        // Remove the event from all users' liked and saved collections
        for (UserEntity user : event.getLiked()) {
            user.getLiked().remove(event);
        }
        for (UserEntity user : event.getSaved()) {
            user.getSaved().remove(event);
        }
        // Clear the associations in the event itself
        event.getLiked().clear();
        event.getSaved().clear();

        eventRepository.delete(event);
    }

    @Override
    public void addEvent(MultipartFile eventImage, Event event) throws IOException {
        //save file name in db
        if(!eventImage.isEmpty()){
            //Create directory
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            //create if not exist
            if(!Files.exists(uploadDirPath)) {
                Files.createDirectory(uploadDirPath);
            }
            uploadFile(eventImage,event,uploadDirPath);
        }
        eventRepository.save(event);
    }

    @Override
    public void updateEvent(MultipartFile eventImage, Event event, Event existingEvent) throws IOException {

        if(!eventImage.isEmpty()){
            //Create directory
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            //create if not exist
            if(!Files.exists(uploadDirPath)) {
                Files.createDirectory(uploadDirPath);
            }
            //Get old Image
            String oldImage = existingEvent.getEventImage();
            if(oldImage!=null){
                Path OldfilePath = uploadDirPath.resolve(oldImage);
                if(!Files.exists(OldfilePath)) {
                    Files.delete(OldfilePath);
                }
            }
            uploadFile(eventImage,event,uploadDirPath);

        }
        else {
            // If no new image is provided, keep the old image
            event.setEventImage(existingEvent.getEventImage());
        }
        eventRepository.save(event);
    }

    //User

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    @Override
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // Likes and Saves adn comments

    @Override
    public List<Comment> getCommentsByEvent(Event event) {
        return (List<Comment>) commentRepository.findAllByEventCommented(event);
    }

    @Override
    public boolean isEventLikedByUser(Event event, UserEntity user) {
        return event.getLiked() != null && event.getLiked().contains(user);
    }

    @Override
    public boolean isEventSavedByUser(Event event, UserEntity user) {
        return event.getSaved() != null && event.getSaved().contains(user);
    }

    @Override
    public void likeEvent(Event event, UserEntity user) {
        boolean isLiked = isEventLikedByUser(event, user);
        if(!isLiked){
            event.getLiked().add(user);
            user.getLiked().add(event);
            eventRepository.save(event);
            userRepository.save(user);
        }
    }

    @Override
    public void saveEvent(Event event, UserEntity user) {
        boolean isSaved = isEventSavedByUser(event, user);
        if(!isSaved){
            event.getSaved().add(user);
            user.getSaved().add(event);

            eventRepository.save(event);
            userRepository.save(user);
        }
    }

    @Override
    public List<Event> getSavedEvents(UserEntity user) {
        if(user == null)
            return List.of();

        return (List<Event>) user.getSaved();
    }

    @Override
    public List<Event> getLikedEvents(UserEntity user) {
        if(user == null)
            return List.of();
        return (List<Event>) user.getLiked();
    }

    @Override
    public void unlikeEvent(Event event, UserEntity user) {
        event.getLiked().remove(user);
        user.getLiked().remove(event);
        eventRepository.save(event);
        userRepository.save(user);
    }

    @Override
    public void unSaveEvent(Event event, UserEntity user) {
        event.getSaved().remove(user);
        user.getSaved().remove(event);
        eventRepository.save(event);
        userRepository.save(user);
    }

    @Override
    public void addComment(Comment comment, UserEntity user, Event event) {
        if(!comment.getContent().isEmpty()){
            comment.setEventCommented(event);
            comment.setUserEntityCommented(user);
            comment.setCommentedAt(new Date());
            commentRepository.save(comment);
        }
    }
    @Override
    public void deleteComment(Long commentId, Long userId, Long eventId){
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            throw new RuntimeException("Comment not found");
        }
        if(comment.getUserEntityCommented().getUserId() == userId.longValue()){
            commentRepository.delete(comment);
        }
    }


    // functional methods
    private void uploadFile(MultipartFile eventImage, Event event,Path uploadDirPath) throws IOException {

        String[] getExtension = eventImage.getOriginalFilename().split("\\.");
        String fileName = UUID.randomUUID() +"."+getExtension[getExtension.length-1].toLowerCase();
        event.setEventImage(fileName);

        //map file name to the path
        //move file to the dir
        Path filePath = uploadDirPath.resolve(fileName);

        try (InputStream inputStream = eventImage.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
