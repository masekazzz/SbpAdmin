package ru.sbp.apihandlers;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.sbp.objects.structures.OrderStructure;
import ru.sbp.objects.structures.ReceiptStructure;
import ru.sbp.objects.db.QR;


public class RequestHandler {
    private static final String URL = "https://pay-test.raif.ru/api";
    private static final String URL_FISCAL = "https://test.ecom.raiffeisen.ru/api/fiscal/v1/receipts/sell";

    private static String getUrl() {
        return URL;
    }

    public static ResponseEntity<OrderStructure> createOrder(String qrId, String token,
                                                             double amount) {
        OrderStructure newOrder = new OrderStructure();
        newOrder.setAmount(amount);
        QR qr = new QR(qrId);
        newOrder.setQr(qr);

        return RequestHandler.createNewOrder(newOrder, token);
    }

    public static ResponseEntity<String> linkUrl(String bearerToken, String callbackUrl) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(RequestHandler.getUrl() + "/settings/v1/callback", HttpMethod.POST,
                new HttpEntity<>("{\"callbackUrl\": \"" + callbackUrl + "\"}",
                        createHeaders(
                                "Bearer " + bearerToken)), String.class);
    }

    public static ResponseEntity<String> deleteOrder(String orderId, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(RequestHandler.getUrl() + "/payment/v1/orders/" + orderId, HttpMethod.DELETE,
                new HttpEntity<>(createHeaders(
                        "Bearer " + bearerToken)), String.class);
    }

    public static ResponseEntity<OrderStructure> createNewOrder(OrderStructure newOrder, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(RequestHandler.getUrl() + "/payment/v1/orders", HttpMethod.POST,
                new HttpEntity<>(newOrder,
                        createHeaders(
                                "Bearer " + bearerToken)), OrderStructure.class);
    }

    public static ResponseEntity<OrderStructure> getOrder(String orderId, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        var answer = restTemplate.exchange(RequestHandler.getUrl() + "/payment/v1/orders/" + orderId, HttpMethod.GET,
                new HttpEntity<>(null, createHeaders(
                        "Bearer " + bearerToken)), OrderStructure.class);
        if (!answer.getBody().getStatus().getValue().equals("NEW")) {
            return ResponseEntity.ok().body(new OrderStructure(answer.getBody().getQr()));
        }
        return restTemplate.exchange(RequestHandler.getUrl() + "/payment/v1/orders/" + orderId, HttpMethod.GET,
                new HttpEntity<>(null, createHeaders(
                        "Bearer " + bearerToken)), OrderStructure.class);
    }

    private static HttpHeaders createHeaders(String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", headerValue);
        return headers;
    }

    public static ResponseEntity<ReceiptStructure> createReceipt(ReceiptStructure receipt, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(RequestHandler.URL_FISCAL, HttpMethod.POST,
                new HttpEntity<>(receipt,
                        createHeaders(
                                "Bearer " + bearerToken)), ReceiptStructure.class);
    }

    public static ResponseEntity<ReceiptStructure> saveReceipt(String receiptId, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(RequestHandler.URL_FISCAL + "/" + receiptId, HttpMethod.PUT,
                new HttpEntity<>(
                        createHeaders(
                                "Bearer " + bearerToken)), ReceiptStructure.class);
    }

    public static ResponseEntity<String> createQrWithRedirect(JSONObject responseBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                        RequestHandler.getUrl() + "/sbp/v2/qrs",
                        HttpMethod.POST,
                        new HttpEntity<>(responseBody.toString(), headers),
                        String.class);
    }
}
