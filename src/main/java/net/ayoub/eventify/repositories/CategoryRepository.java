package net.ayoub.eventify.repositories;

import net.ayoub.eventify.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
