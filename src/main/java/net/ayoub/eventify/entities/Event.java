package net.ayoub.eventify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    @NotBlank(message = "Event must have a title")
    private String title;
    @NotBlank(message = "Event must have a description")
    private String description;
    @NotBlank(message = "You must specify the country")
    private String country;
    @NotBlank(message = "You must specify the country Event")
    private String city;
    @NotBlank
    private String address;


    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;

    private String eventImage;
    @Min(0)
    private int ticketNumber;

    //Association with Category
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "category_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Collection<Category> category = new ArrayList<>();

    //Comments
    @OneToMany(mappedBy="eventCommented", cascade = CascadeType.ALL)
    private Collection<Comment> comments = new ArrayList<>();

    //Likes
    @ManyToMany(mappedBy = "liked")
    private Collection<UserEntity> liked = new ArrayList<>();
    //Likes
    @ManyToMany(mappedBy = "saved")
    private Collection<UserEntity> saved = new ArrayList<>();

}
