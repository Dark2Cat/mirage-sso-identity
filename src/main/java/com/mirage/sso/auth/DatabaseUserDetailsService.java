package com.mirage.sso.auth;

import com.mirage.sso.user.UserEntity;
import com.mirage.sso.user.UserService;
import com.mirage.sso.user.UserStatus;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public DatabaseUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));

        boolean enabled = user.getStatus() == UserStatus.ACTIVE;
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("sso:user:read"),
                new SimpleGrantedAuthority("sso:client:manage"),
                new SimpleGrantedAuthority("sso:audit:read")
        );

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.getStatus() == UserStatus.LOCKED)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }
}
