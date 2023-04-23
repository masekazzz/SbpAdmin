package ru.sbp.objects.auth;

import lombok.Data;
import ru.sbp.objects.db.QR;

import java.util.List;

@Data
public class JwtResponse {

    private Long id;
    private String username;
    private String accessToken;
    private String refreshToken;
    private List<QR> qrs;
}
