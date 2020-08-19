package com.skillbox.socialnetwork.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(2)
//https://stackoverflow.com/questions/44302457/how-can-i-implement-basic-authentication-with-jwt-authentication-in-spring-boot
class BasicAuthSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/actuator/**")
                .authorizeRequests()
                .antMatchers("/actuator/**").hasRole("USER")
                .antMatchers("/logout").permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("actuator")
                .password("$2y$12$VZwlZa9M1v0N5r.QurUj.O18OAuNrnjUXgemOgqdhJjw.pTgvsVwi")
                .roles("USER");
    }
}