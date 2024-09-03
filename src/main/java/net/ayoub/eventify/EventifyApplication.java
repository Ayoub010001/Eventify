package net.ayoub.eventify;

import net.ayoub.eventify.entities.Category;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.CategoryRepository;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.stream.Stream;


@SpringBootApplication
public class EventifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventifyApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(EventRepository eventRepository,
										UserRepository	userRepository,
										CategoryRepository categoryRepository, RolesRepository rolesRepository) {

		return args -> {
			Stream.of("Ayoub", "Yahya", "Ismail").forEach(name -> {
				UserEntity user = new UserEntity();
				user.setUserName(name);
				user.setPassword("password");
				user.setEmail(name+"@gmail.com");
				userRepository.save(user);
			});

			Role role = new Role();
			role.setRoleName("ROLE_USER");
			rolesRepository.save(role);

			Stream.of("Music", "Gaming").forEach(name -> {
				Category category = new Category();
				category.setCategoryName(name);
				categoryRepository.save(category);
			});

			Stream.of("GamingExpo", "I3", "GamesCom").forEach(name -> {
				Event event = new Event();
				event.setTitle(name);
				event.setDescription(name + " description...");
				eventRepository.save(event);
			});
		};

	};

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
