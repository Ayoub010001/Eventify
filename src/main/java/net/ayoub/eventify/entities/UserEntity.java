package net.ayoub.eventify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ayoub.eventify.security.entities.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "username should not be empty")
    @Size(min = 3, max = 25, message = "username should be at least 3 characters")
    private String userName;
    @NotEmpty(message = "password should not be empty")
    @Size(min = 4, max = 225, message = "password should be at least 4 characters")
    private String password;
    @Email(message = "invalid email address")
    @NotEmpty(message = "email should not be empty")
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();

    //Comment
    @OneToMany(mappedBy = "userEntityCommented", cascade = CascadeType.ALL)
    private Collection<Comment> comments = new ArrayList<>();

    //Likes
    @ManyToMany
    @JoinTable(name = "likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
            //,uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"})
    )
    private Collection<Event> liked = new ArrayList<>();

    //Saves
    @ManyToMany
    @JoinTable(name = "saved",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
            //,uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"})
    )
    private Collection<Event> saved = new ArrayList<>();
}
