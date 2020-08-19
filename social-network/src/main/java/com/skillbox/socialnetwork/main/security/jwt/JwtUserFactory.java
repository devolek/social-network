package com.skillbox.socialnetwork.main.security.jwt;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
                user.getCity(),
                user.getCountry(),
                user.getConfirmationCode(),
                user.getIsApproved(),
                user.getMessagesPermission(),
                user.getStatus(),
                user.getLastOnlineTime(),
                user.getIsBlocked(),
                getAuthorities(user.getRoles())
        );
    }

    private static List<GrantedAuthority> getAuthorities(List<Role> userRoles){
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName().toString())
                ).collect(Collectors.toList());
    }
}
