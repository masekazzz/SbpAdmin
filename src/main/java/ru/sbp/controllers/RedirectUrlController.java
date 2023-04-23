package ru.sbp.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sbp.apihandlers.RequestHandler;
import ru.sbp.objects.db.QR;
import ru.sbp.storage.Context;
import ru.sbp.utils.DbHandler;

import java.util.Objects;

@RestController
public class RedirectUrlController {
    @Autowired
    private Context context;

    @PostMapping(path="/createQrWithRedirect")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> createQrWithRedirect(@RequestBody JSONObject qrInfo) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("qrType", "QRVariable");

        if (qrInfo.has("additionalInfo")) {
            json.put("additionalInfo", qrInfo.getString("additionalInfo"));
        }
        if (qrInfo.has("paymentDetails")) {
            json.put("paymentDetails", qrInfo.getString("paymentDetails"));
        }

        json.put("sbpMerchantId", context.getSbpMerchantId());

        String qrRedirectUrlId = java.util.UUID.randomUUID().toString();

        json.put("redirectUrl", context.getRedirectUrl() + qrRedirectUrlId);

        var res = RequestHandler.createQrWithRedirect(json);

        JSONObject response = new JSONObject(Objects.requireNonNull(res.getBody()));

        response.put("redirectUrl", context.getRedirectUrl() + qrRedirectUrlId);

        QR qr = DbHandler.getQrById(response.getString("qrId"));
        qr.setRedirectUrlId(qrRedirectUrlId);
        DbHandler.updateQr(qr);
        return ResponseEntity.ok().body(response.toString());
    }
}
