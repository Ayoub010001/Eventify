package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
