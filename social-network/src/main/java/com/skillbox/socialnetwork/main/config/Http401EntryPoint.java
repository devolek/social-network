package com.skillbox.socialnetwork.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class Http401EntryPoint implements AuthenticationEntryPoint {

    public static Http401EntryPoint unauthorizedHandler(){
        return new Http401EntryPoint();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("Auth error");
        ObjectMapper mapper = new ObjectMapper();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response
                .getOutputStream()
                .println(mapper.writeValueAsString(ResponseFactory.getErrorResponse("invalid request", "unauthorized")));
    }

}
