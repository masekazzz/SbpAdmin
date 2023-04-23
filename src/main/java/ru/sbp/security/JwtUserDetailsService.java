package ru.sbp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sbp.objects.db.User;
import ru.sbp.utils.DbHandler;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = DbHandler.getUserByUsername(username);
        return JwtEntityFactory.create(user);
    }
}
