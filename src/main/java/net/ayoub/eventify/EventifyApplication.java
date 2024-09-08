package net.ayoub.eventify;

import net.ayoub.eventify.entities.Category;
import net.ayoub.eventify.entities.Event;
import net.ayoub.eventify.entities.UserEntity;
import net.ayoub.eventify.repositories.CategoryRepository;
import net.ayoub.eventify.repositories.EventRepository;
import net.ayoub.eventify.repositories.UserRepository;
import net.ayoub.eventify.security.CustomUserDetailService;
import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.repository.RolesRepository;
import net.ayoub.eventify.security.service.UserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


@SpringBootApplication
public class EventifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventifyApplication.class, args);
	}

	//@Bean
	CommandLineRunner users(UserDetailsService userDetailsService) {
		return args -> {
			userDetailsService.addRole("ROLE_USER");
			userDetailsService.addRole("ROLE_ADMIN");
			userDetailsService.addUser("aub","1234","null@null.com");
			userDetailsService.addUser("admin","1234","null@null.com");

			userDetailsService.addRoleToUser("aub","ROLE_USER");
			userDetailsService.addRoleToUser("admin","ROLE_ADMIN");
		};
	}

	//@Bean
	CommandLineRunner commandLineRunner(EventRepository eventRepository,
										UserRepository	userRepository,
										CategoryRepository categoryRepository, RolesRepository rolesRepository) {

		return args -> {
			Stream.of("Music", "Gaming").forEach(name -> {
				Category category = new Category();
				category.setCategoryName(name);
				categoryRepository.save(category);
			});

			Stream.of("GamingExpo", "I3", "GamesCom").forEach(name -> {
				Event event = new Event();
				event.setTitle(name);
				event.setDescription(name + " description...");
				event.setCountry("Morocco");
				event.setCity("Casablanca");
				event.setAddress("Technopark");
				eventRepository.save(event);
			});
		};

	};

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
