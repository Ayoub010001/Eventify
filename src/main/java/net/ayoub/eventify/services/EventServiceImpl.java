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
import java.util.List;
import java.util.UUID;


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

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    @Override
    public Event deleteEvent(Long eventId) throws IOException {
        //Find event
        Event event = getEventById(eventId);

        if(event.getEventImage() != null){
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            Path filePath = uploadDirPath.resolve(event.getEventImage().toString());

            if(Files.exists(filePath)){
                Files.delete(filePath);
                System.out.println("File deleted: " + filePath);
            }else {
                System.out.println("File does not exist: " + filePath);
            }
        }
        eventRepository.delete(event);
        return null;
    }

    @Override
    public Event addEvent(MultipartFile eventImage,Event event) throws IOException {
        //save file name in db
        if(!eventImage.isEmpty()){
            String[] getExtension = eventImage.getOriginalFilename().split("\\.");
            System.out.println(getExtension.toString());
            String fileName = UUID.randomUUID().toString()+"."+getExtension[getExtension.length-1].toLowerCase();
            event.setEventImage(fileName);
            //Create directory
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            //create if not exist
            if(!Files.exists(uploadDirPath)) {
                Files.createDirectory(uploadDirPath);
            }
            //map file name to the path
            //move file to the dir
            Path filePath = uploadDirPath.resolve(fileName);
            System.out.println("directory=>"+uploadDirPath);
            System.out.println("file =>"+filePath);

            try (InputStream inputStream = eventImage.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(MultipartFile eventImage,Event event) throws IOException {

        return null;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    @Override
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

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
}
