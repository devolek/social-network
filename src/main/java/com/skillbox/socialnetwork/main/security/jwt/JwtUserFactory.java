package com.skillbox.socialnetwork.main.security.jwt;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(Person user){
        return new JwtUser(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRegDate(),
                user.getBirthDate(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getPhoto(),
                user.getAbout(),
                user.getTown(),
                user.getConfirmationCode(),
                user.getApproved(),
                user.getMessagesPermission(),
                user.getLastOnline(),
                user.getBlocked(),
                getAuthorities(user.getRoles())
        );
    }

    private static List<GrantedAuthority> getAuthorities(List<Role> userRoles){
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName())
                ).collect(Collectors.toList());
    }
}
