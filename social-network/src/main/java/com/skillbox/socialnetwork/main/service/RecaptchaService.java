package com.skillbox.socialnetwork.main.service;

public interface RecaptchaService {
    boolean verifyRecaptcha(String ip, String recaptchaResponse);
}
