package com.skillbox.socialnetwork.main.security.jwt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillbox.socialnetwork.main.model.enumerated.Permission;
import com.skillbox.socialnetwork.main.model.enumerated.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@AllArgsConstructor
@Getter
public class JwtUser implements UserDetails {

    private int id;
    private String firstName;
    private String lastName;
    private Date regDate;
    private Date birthDate;
    private String email;
    private String phone;
    @JsonIgnore
    private String password;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String confirmationCode;
    private boolean isApproved;
    private Permission messagePermission;
    private Status status;
    private Date lastOnline;
    private boolean isBlocked;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isApproved;
    }
}
