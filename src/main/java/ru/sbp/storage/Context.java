package ru.sbp.storage;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Context {
    @Value("${context.url}")
    private String url;
    @Value("${context.callback}")
    private String callback;
    @Value("${context.istest}")
    private boolean isTest;
    @Value("${context.phone}")
    private String phone;
    @Value("${context.callbackServerAddr}")
    private String callbackServerAddress;
    @Value("${context.sbpMerchantId}")
    private String sbpMerchantId;
    @Value("${context.redirectUrl}")
    private String redirectUrl;
}
