package net.ayoub.eventify.web;

import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.CommentRepository;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.entities.UserAccount;
//import net.ayoub.eventify.security.repository.UserAccountRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EventController {
    private final String UPLOAD_DIR = "src/main/resources/uploads";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public EventController(EventRepository eventRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    //index path
    @GetMapping("/")
    public String index() {
        return "redirect:/events";
    }
    //home
    @GetMapping("/events")
    public String events(Model model){
        List<Event> events = eventRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String UserName =authentication.getName();
        UserEntity userAccount = userRepository.findByUserName(UserName).orElseThrow(() -> new RuntimeException("User Not Found"));

        model.addAttribute("user", userAccount);
        model.addAttribute("events", events);
        return "index";
    }

    @GetMapping("/event")
    public String event(Model model, @RequestParam("eventId") Long eventId,
                        @RequestParam(value = "userId", defaultValue = "1") Long userId){
        Event event = eventRepository.findById(eventId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);

        //System.out.println("=>"+event.getLiked());

        boolean isLiked = event.getLiked() != null && event.getLiked().contains(user);
        boolean isSaved = event.getSaved() != null && event.getSaved().contains(user);
        List<Comment> comment = (List<Comment>) commentRepository.findAllByEventCommented(event);

        model.addAttribute("event", event);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("userId", userId);

        model.addAttribute("comments", comment);
        model.addAttribute("commentObj", new Comment());
        return "event";
    }

    @GetMapping("/events/new")
    public String newEvent(Model model){
        model.addAttribute("event", new Event());
        return "events/newEvent";
    }

    @PostMapping(value = "/events/save" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveEvent(Model model,
            @RequestParam("file") MultipartFile eventImage,
            Event event) throws IOException {

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
        eventRepository.save(event);
        return "redirect:/events";
    }

    @GetMapping("/events/update")
    public String updateEvent(@RequestParam("eventId") Long eventId, Model model){
        Event event = eventRepository.findById(eventId).orElse(null);
        model.addAttribute("eventUpdate", event);
        return "events/updateEvent";
    }

    @PostMapping(value = "/events/saveUpdates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveUpdateEvent(Model model,@RequestParam("file") MultipartFile eventImage,
                                  Event eventUpdated) throws IOException {

        Event existingEvent = eventRepository.findById(eventUpdated.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventUpdated.getEventId()));

        if(!eventImage.isEmpty()){
            //Create directory
            Path uploadDirPath = Paths.get(UPLOAD_DIR);
            //create if not exist
            if(!Files.exists(uploadDirPath)) {
                Files.createDirectory(uploadDirPath);
            }

            System.out.println("upload =>"+uploadDirPath);
            String oldImage = existingEvent.getEventImage();


            if(oldImage!=null){
                Path OldfilePath = uploadDirPath.resolve(oldImage);
                if(!Files.exists(OldfilePath)) {
                    Files.delete(OldfilePath);
                }
            }

            //naming
            String[] getExtension = eventImage.getOriginalFilename().split("\\.");
            String fileName = UUID.randomUUID().toString()+"."+getExtension[getExtension.length-1].toLowerCase();
            System.out.println("upload fileName =>"+fileName);

            //save to db
            eventUpdated.setEventImage(fileName);

            //map file name to the path
            Path newfilePath = uploadDirPath.resolve(fileName);
            //move file to the dir
            try (InputStream inputStream = eventImage.getInputStream()) {
                Files.copy(inputStream, newfilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        else {
            // If no new image is provided, keep the old image
            eventUpdated.setEventImage(existingEvent.getEventImage());
        }
        eventRepository.save(eventUpdated);
        return "redirect:/events";
    }

    @GetMapping("/events/delete")
    public String deleteEvent(@RequestParam("eventId") Long eventId,
                              //@RequestParam("userId") Long userId,
                              Model model) throws IOException {
        Event event = eventRepository.findById(eventId).orElse(null);

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
        return "redirect:/events";
    }

    @GetMapping("/events/like")
    public String likeEvent(@RequestParam("eventId") Long eventId,
                            @RequestParam(value = "userId", defaultValue = "1") Long userId,
                            Model model){
        Event event = eventRepository.findById(eventId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);

        boolean isLiked = event.getLiked() != null && event.getLiked().contains(user);

        System.out.println("isLiked: " + isLiked);

        if(!isLiked){
            event.getLiked().add(user);
            user.getLiked().add(event);
            eventRepository.save(event);
            userRepository.save(user);
        }

        model.addAttribute("event", event);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }

    @GetMapping("/events/saves")
    public String savedEvent(@RequestParam("eventId") Long eventId,
                            @RequestParam(value = "userId", defaultValue = "1") Long userId,
                            Model model){
        Event event = eventRepository.findById(eventId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);

        boolean isSaved = event.getSaved() != null && event.getSaved().contains(user);
        if(!isSaved){
            event.getSaved().add(user);
            user.getSaved().add(event);

            eventRepository.save(event);
            userRepository.save(user);
        }
        model.addAttribute("event", event);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }

    @GetMapping("/event/savesList")
    public String savesList( @RequestParam(value = "userId", defaultValue = "1") Long userId, Model model){
        UserEntity user = userRepository.findById(userId).orElse(null);
        List<Event> events = (List<Event>) user.getSaved();
        model.addAttribute("events", events);
        return "event/saves";
    }
    @GetMapping("/event/likesList")
    public String likesList(@RequestParam(value = "userId", defaultValue = "1") Long userId, Model model){
        UserEntity user = userRepository.findById(userId).orElse(null);
        List<Event> events = (List<Event>) user.getLiked();
        model.addAttribute("events", events);
        return "event/likes";
    }

    @GetMapping("/event/unlike")
    public String unlike(@RequestParam("eventId") Long eventId,
            @RequestParam(value = "userId", defaultValue = "1") Long userId, Model model){
        Event event = eventRepository.findById(eventId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);
        event.getLiked().remove(user);
        user.getLiked().remove(event);
        eventRepository.save(event);
        userRepository.save(user);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }

    @GetMapping("/event/unsave")
    public String unsave(@RequestParam("eventId") Long eventId,
                         @RequestParam(value = "userId", defaultValue = "1") Long userId, Model model){
        Event event = eventRepository.findById(eventId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);
        event.getSaved().remove(user);
        user.getSaved().remove(event);
        eventRepository.save(event);
        userRepository.save(user);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }

    @PostMapping("/event/comment")
    public String addComment(@RequestParam("eventId") Long eventId,
                             @RequestParam(value = "userId", defaultValue = "1") Long userId,
                             Comment comment, Model model){

        UserEntity user = userRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);
        comment.setEventCommented(event);
        comment.setUserEntityCommented(user);
        comment.setCommentedAt(new Date());

        commentRepository.save(comment);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    @GetMapping("/event/comment/delete")
    public String deleteComment(@RequestParam("eventId") Long eventId,
                                @RequestParam(value = "userId", defaultValue = "1") Long userId,
                                @RequestParam(value = "commentId") Long commentId, Model model){

        //find comment
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            throw new RuntimeException("Comment not found");
        }
        if(comment.getUserEntityCommented().getUserId() == userId.longValue()){
            commentRepository.delete(comment);
        }
        //delete comment
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
}
