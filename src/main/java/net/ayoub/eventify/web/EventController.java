package net.ayoub.eventify.web;

import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import org.springframework.http.MediaType;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EventController {
    private final String UPLOAD_DIR = "src/main/resources/uploads";
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
        model.addAttribute("events", events);
        return "index";
    }

    @GetMapping("/event")
    public String event(Model model, @RequestParam("eventId") Long eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
        model.addAttribute("event", event);
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
    public String deleteEvent(@RequestParam("eventId") Long eventId, Model model) throws IOException {
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
        eventRepository.delete(event);
        return "redirect:/events";
    }


}
