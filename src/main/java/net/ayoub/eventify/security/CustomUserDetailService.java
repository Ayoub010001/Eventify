package net.ayoub.eventify.security;

import net.ayoub.eventify.security.entities.Role;
import net.ayoub.eventify.security.entities.UserAccount;
import net.ayoub.eventify.security.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
        return new User(userAccount.getUserName(),userAccount.getPassword(),getAuthorities(userAccount.getRoles()));
    }

    public Collection<GrantedAuthority> getAuthorities(List<Role> roles) {
            return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .collect(Collectors.toList());
    }
}
