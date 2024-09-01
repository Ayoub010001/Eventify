package net.ayoub.eventify.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String password;
    private String email;

    //Comment
    @OneToMany(mappedBy = "userCommented")
    private Collection<Comment> comments = new ArrayList<>();

    //Likes
    @ManyToMany
    @JoinTable(name = "likes")
    private Collection<Event> liked = new ArrayList<>();

    //Saves
    @ManyToMany
    @JoinTable(name = "saved")
    private Collection<Event> saved = new ArrayList<>();
}
