package com.java.moveminds.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final String email;

    public CustomUserDetails(String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = email;
    }

}
