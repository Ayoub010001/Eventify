package net.ayoub.eventify.web;

import jakarta.validation.Valid;
import net.ayoub.eventify.entities.Comment;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.CommentRepository;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.entities.UserAccount;
//import net.ayoub.eventify.security.repository.UserAccountRepository;
import net.ayoub.eventify.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final double UPLOAD_MAX_SIZE = 4 * 1024 * 1024;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    private EventService eventService;

    public EventController(EventRepository eventRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    //index path ✅
    @GetMapping("/")
    public String index() {
        return "redirect:/events";
    }
    //home ✅
    @GetMapping("/events")
    public String events(Model model){
        //List<Event> events = eventRepository.findAll();
        List<Event> events = eventService.getAllEvents();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String UserName =authentication.getName();
        //UserEntity userAccount = userRepository.findByUserName(UserName).orElse(null);
        UserEntity userAccount = eventService.getUserByUsername(UserName);
        if(userAccount != null){
            model.addAttribute("user", userAccount);
        }
        model.addAttribute("events", events);
        return "index";
    }

    // refactoring ✅
    @GetMapping("/event")
    public String event(Model model, @RequestParam("eventId") Long eventId,
                        @RequestParam(value = "userId") Long userId){
        //Event event = eventRepository.findById(eventId).orElse(null);
        //UserEntity user = userRepository.findById(userId).orElse(null);

        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);

        boolean isLiked = eventService.isEventLikedByUser(event, user);
        boolean isSaved = eventService.isEventSavedByUser(event, user);

        //List<Comment> comment = (List<Comment>) commentRepository.findAllByEventCommented(event);
        List<Comment> comment = eventService.getCommentsByEvent(event);

        model.addAttribute("event", event);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("userId", userId);
        model.addAttribute("comments", comment);
        model.addAttribute("commentObj", new Comment());
        return "event";
    }

    // refactoring ✅
    @GetMapping("/events/new")
    public String newEvent(Model model){
        model.addAttribute("event", new Event());
        return "events/newEvent";
    }

    // TODO✅
    @PostMapping(value = "/events/save" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveEvent(Model model,
                            @RequestParam("file") MultipartFile eventImage,
                            @Valid @ModelAttribute("event") Event event, BindingResult result) throws IOException {

        if (eventImage.getSize() > UPLOAD_MAX_SIZE) {
            result.rejectValue("eventImage", "error.event", "Image size cannot exceed 5MB.");
            return "events/newEvent";
        }
        if (result.hasErrors()) {
            return "events/newEvent";
        }

        // Manually validate file size
        long maxSizeInBytes = 5 * 1024 * 1024; // 5MB limit
        if (eventImage.isEmpty() && eventImage.getSize() > maxSizeInBytes) {
            result.rejectValue("eventImage", "error.eventImage", "File size exceeds the maximum allowed limit of 5MB");
        }

        //save file name in db
        if(!result.hasErrors()){
            eventService.addEvent(eventImage,event);
        }

        return "redirect:/events";
    }

    //// refactoring ✅
    @GetMapping("/events/update")
    public String updateEvent(@RequestParam("eventId") Long eventId, Model model){
        //Event event = eventRepository.findById(eventId).orElse(null);
        Event event = eventService.getEventById(eventId);
        model.addAttribute("eventUpdate", event);
        return "events/updateEvent";
    }

    // TODO✅
    @PostMapping(value = "/events/saveUpdates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveUpdateEvent(Model model,@RequestParam("file") MultipartFile eventImage,
                                  @Valid @ModelAttribute("eventUpdate") Event eventUpdated, BindingResult result) throws IOException {

        Event existingEvent = eventService.getEventById(eventUpdated.getEventId());

        if(result.hasErrors()){
            return "events/updateEvent";
        }

        if (eventImage.getSize() > UPLOAD_MAX_SIZE) {
            result.rejectValue("eventImage", "error.event", "Image size cannot exceed 5MB.");
            return "events/updateEvent";
        }
        eventService.updateEvent(eventImage,eventUpdated,existingEvent);
        return "redirect:/events";
    }
    // TODO✅
    @GetMapping("/events/delete")
    public String deleteEvent(@RequestParam("eventId") Long eventId,
                              //@RequestParam("userId") Long userId,
                              Model model) throws IOException {
        eventService.deleteEvent(eventId);
        return "redirect:/events";
    }
    // TODO✅
    @GetMapping("/events/like")
    public String likeEvent(@RequestParam("eventId") Long eventId,
                            @RequestParam(value = "userId") Long userId,
                            Model model){
        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);
        eventService.likeEvent(event, user);
        model.addAttribute("event", event);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    // TODO✅
    @GetMapping("/events/saves")
    public String savedEvent(@RequestParam("eventId") Long eventId,
                            @RequestParam(value = "userId") Long userId,
                            Model model){
        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);
        eventService.saveEvent(event,user);
        model.addAttribute("event", event);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    // TODO✅
    @GetMapping("/event/savesList")
    public String savesList( @RequestParam(value = "userId") Long userId, Model model){
        UserEntity user = eventService.getUserById(userId);
        List<Event> events = eventService.getSavedEvents(user);
        model.addAttribute("userId",userId);
        model.addAttribute("events", events);
        return "event/saves";
    }
    // TODO✅
    @GetMapping("/event/likesList")
    public String likesList(@RequestParam(value = "userId") Long userId, Model model){
        UserEntity user = eventService.getUserById(userId);
        List<Event> events = eventService.getLikedEvents(user);
        model.addAttribute("userId",userId);
        model.addAttribute("events", events);
        return "event/likes";
    }
    // TODO✅
    @GetMapping("/event/unlike")
    public String unlike(@RequestParam("eventId") Long eventId,
            @RequestParam(value = "userId") Long userId, Model model){
        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);
        eventService.unlikeEvent(event,user);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    // TODO✅
    @GetMapping("/event/unsave")
    public String unsave(@RequestParam("eventId") Long eventId,
                         @RequestParam(value = "userId") Long userId, Model model){
        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);
        eventService.unSaveEvent(event,user);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    // TODO✅
    @PostMapping("/event/comment")
    public String addComment(@RequestParam("eventId") Long eventId,
                             @RequestParam(value = "userId") Long userId,
                             Comment comment, Model model){
        Event event = eventService.getEventById(eventId);
        UserEntity user = eventService.getUserById(userId);
        eventService.addComment(comment,user,event);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
    // TODO✅
    @GetMapping("/event/comment/delete")
    public String deleteComment(@RequestParam("eventId") Long eventId,
                                @RequestParam(value = "userId") Long userId,
                                @RequestParam(value = "commentId") Long commentId, Model model){
        eventService.deleteComment(commentId,userId,eventId);
        return "redirect:/event?eventId=" + eventId + "&userId=" + userId;
    }
}
