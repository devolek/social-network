package com.skillbox.socialnetwork.main.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.skillbox.socialnetwork.main.dto.GeoIP.GeoIP;
import com.skillbox.socialnetwork.main.dto.auth.request.AuthenticationRequestDto;
import com.skillbox.socialnetwork.main.dto.auth.request.RegisterRequestDto;
import com.skillbox.socialnetwork.main.dto.profile.request.EmailRequestDto;
import com.skillbox.socialnetwork.main.dto.profile.request.PasswordSetRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.model.Person;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AuthService {
    Response login(AuthenticationRequestDto request, HttpServletRequest httpServletRequest, String referer);

    Response register(RegisterRequestDto requestDTO, GeoIP location, HttpServletRequest request) throws IOException, GeoIp2Exception;

    void logout(String token);

    Person getAuthorizedUser(String token);

    @Deprecated
    boolean isAuthorized(String token);

    Response passwordRecovery(String email, String url, String viewName);

    Response passwordSet(PasswordSetRequestDto dto, String referer);

    Response sendEmailChangeLetter(HttpServletRequest request, String token);

    Response changeEmail(String token, EmailRequestDto request, String referer);

    boolean sendActivationEmail(HttpServletRequest request, String email);
}
