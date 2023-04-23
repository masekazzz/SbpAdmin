package ru.sbp.controllers;

import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.sbp.apihandlers.AuthHandler;
import ru.sbp.objects.structures.OrderStructure;
import ru.sbp.apihandlers.RequestHandler;
import ru.sbp.objects.stats.QrStatistics;
import ru.sbp.objects.structures.SecretKey;
import ru.sbp.objects.auth.JwtRequest;
import ru.sbp.objects.auth.JwtResponse;
import ru.sbp.objects.db.Order;
import ru.sbp.objects.db.QR;
import ru.sbp.objects.db.User;
import ru.sbp.security.JwtEntity;
import ru.sbp.storage.Context;
import ru.sbp.utils.DbHandler;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static ru.sbp.utils.DbHandler.*;

@RestController
@RequiredArgsConstructor
public class MobileController {
    @Autowired
    private Context context;

    private final AuthHandler authHandler;
    private final PasswordEncoder passwordEncoder;

//    private static final org.apache.logging.log4j.core.Logger logger = (Logger) LogManager.getLogger(MobileController.class);

    @PostMapping(path = "/createOrder")
    public ResponseEntity<OrderStructure> createOrder(@RequestBody OrderStructure newOrder, Authentication authentication) {
        QR qr = getQrByOrder(newOrder);
        Long userId = ((JwtEntity) authentication.getPrincipal()).getId();
        User user = DbHandler.getUserById(userId);
        if (qr.getCurrentOrderId() == null || qr.getCurrentOrderId().isEmpty()) {
            var res = RequestHandler.createNewOrder(newOrder, user.getSecretKey());
            changeCurrentOrderId(res.getBody());
            return ResponseEntity.ok().body(res.getBody());
        } else {
            try {
                RequestHandler.deleteOrder(qr.getCurrentOrderId(), user.getSecretKey());
            } catch (Exception ignored) {
            }
            var res = RequestHandler.createNewOrder(newOrder, user.getSecretKey());
            changeCurrentOrderId(res.getBody());
            res.getBody().getQr().setRedirectUrlId(qr.getRedirectUrlId());
            res.getBody().getQr().setFiscType(qr.getFiscType());
            return ResponseEntity.ok().body(res.getBody());
        }
    }

    @PutMapping(path = "/{qrId}/setFiscType/{fiscType}")
    public ResponseEntity<String> setFiscType(@PathVariable String qrId, @PathVariable String fiscType) {
        // no fisc - None/NOFISC, 1.05 - FISC105, 1.2 - FISC12
//        logger.info("setFiscType, login " + login + " fiscType " + fiscType);
        DbHandler.updateUserFiscType(fiscType, qrId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/getOrder/{qrId}")
    public ResponseEntity<OrderStructure> getOrder(@PathVariable String qrId, Authentication authentication) {
//        logger.info("Get order request, qrId " + qrId);
        QR qr = getQrById(qrId);
        Long userId = ((JwtEntity) authentication.getPrincipal()).getId();
        User user = DbHandler.getUserById(userId);
        DbHandler.linkQrWithUser(qrId, userId);
        if (qr.getCurrentOrderId() == null || qr.getCurrentOrderId().isEmpty()) {
            return ResponseEntity.ok().body(new OrderStructure(new QR(qr.getId())));
        } else {
            var result = RequestHandler.getOrder(qr.getCurrentOrderId(), user.getSecretKey());
            result.getBody().getQr().setRedirectUrlId(qr.getRedirectUrlId());
            result.getBody().getQr().setFiscType(qr.getFiscType());
            return result;
        }
    }

    @PutMapping(path = "/setSecretKey")
    public ResponseEntity<String> changeSecretKey(@RequestBody SecretKey body, Authentication authentication) {
        Long userId = ((JwtEntity) authentication.getPrincipal()).getId();
        try {
            RequestHandler.linkUrl(body.getSecretKey(), context.getCallbackServerAddress());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DbHandler.setSecretKey(userId, body.getSecretKey());
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/getStats/{qrId}")
    public ResponseEntity<QrStatistics> getStats(@PathVariable String qrId){
        QR qr = getQrById(qrId);
        List<Order> orderList = getOrdersByQrId(qrId);
        return ResponseEntity.ok().body(new QrStatistics(qr.getTotalSum(), qr.getOrderCount(), orderList));
    }

    @DeleteMapping (path = "/clearStats/{qrId}")
    public ResponseEntity<String> clearStats(@PathVariable String qrId){
        DbHandler.clearStats(qrId);
        return ResponseEntity.ok().build();
    }

    @PostMapping (path = "/auth/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest loginRequest){
        authHandler.login(loginRequest);
        return ResponseEntity.ok().body(authHandler.login(loginRequest));
    }

    @PostMapping (path = "/auth/register")
    public ResponseEntity<User> register(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        DbHandler.createUser(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping (path = "/removeLink/{qrId}")
    public ResponseEntity<String> removeLink(@PathVariable String qrId, Authentication authentication){
        Long userId = ((JwtEntity) authentication.getPrincipal()).getId();
        DbHandler.deleteLink(userId, qrId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody String refreshToken) throws AccessDeniedException {
        return ResponseEntity.ok().body(authHandler.refresh(refreshToken));
    }
}
