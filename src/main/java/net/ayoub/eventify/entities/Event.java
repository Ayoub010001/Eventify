package net.ayoub.eventify.entities;

import jakarta.persistence.*;
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
    private String title;
    private String description;
    private String country;
    private String city;
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;

    private String eventImage;
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
