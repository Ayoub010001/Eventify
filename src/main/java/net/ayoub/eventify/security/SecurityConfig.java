package net.ayoub.eventify.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.
                csrf(AbstractHttpConfigurer::disable).
                userDetailsService(userDetailsService).
                authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/events/delete",
                                 "/events/update","/events/new",
                                 "/events/save","/events/saveUpdates").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/events", true)// Custom login page
                        .permitAll()  // Permit access to the login page
                )
                .exceptionHandling( exception -> exception.accessDeniedPage("/notAuthorized"))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .build();
    }

    //Don't use InMemoryDetailsService with Custom details service
    //@Bean
    public UserDetailsService inMemoryUsers() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER").build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .roles("USER","ADMIN").build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
    }


}

