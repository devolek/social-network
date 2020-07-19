package com.skillbox.socialnetwork.main.config;

import com.skillbox.socialnetwork.main.security.jwt.JwtConfigurer;
import com.skillbox.socialnetwork.main.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers(
                            "/api/v1/auth/*",
                            "/api/v1/account/register",
                            "/api/v1/account/password/recovery",
                            "/api/v1/account/password/set",
                            "/api/v1/platform/languages",
                            "/api/v1/storage/*"
                    ).permitAll()
                    .antMatchers("/api/v1/**").hasRole("USER")
                    .antMatchers("/actuator/","/actuator/**").hasRole("USER")
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and().exceptionHandling().authenticationEntryPoint(Http401EntryPoint.unauthorizedHandler());
    }
}
