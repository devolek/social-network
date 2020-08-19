package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.service.RecaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RecaptchaServiceImpl implements RecaptchaService {
    private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";
    @Value("${recaptcha.secret}")
    String recaptchaSecret;
    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public boolean verifyRecaptcha(String ip,
                                   String recaptchaResponse) {
        Map<String, String> body = new HashMap<>();
        body.put("secret", recaptchaSecret);
        body.put("response", recaptchaResponse);
        body.put("remoteip", ip);
        log.debug("Request body for recaptcha: {}", body);
        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder.build()
                        .postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL +
                                        "?secret={secret}&response={response}&remoteip={remoteip}",
                                body, Map.class, body);

        log.debug("Response from recaptcha: {}",
                recaptchaResponseEntity);
        Map<String, Object> responseBody =
                recaptchaResponseEntity.getBody();

        log.info("success: {}, score: {}", responseBody.get("success"), responseBody.get("score"));

        return (boolean) responseBody.get("success") && (double) responseBody.get("score") > 0.7;
    }
}
