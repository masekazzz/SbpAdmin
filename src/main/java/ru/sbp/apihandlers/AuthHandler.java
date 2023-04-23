package ru.sbp.apihandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.sbp.objects.auth.JwtRequest;
import ru.sbp.objects.auth.JwtResponse;
import ru.sbp.objects.db.User;
import ru.sbp.security.JwtTokenProvider;
import ru.sbp.utils.DbHandler;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse login(JwtRequest request){
        JwtResponse jwtResponse = new JwtResponse();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = DbHandler.getUserByUsername(request.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(user.getId(), user.getUsername()));
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername()));
        jwtResponse.setQrs(DbHandler.getQrsByUserId(user.getId()));
        return jwtResponse;
    }

    public JwtResponse refresh(String refreshToken) throws AccessDeniedException {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }

}
