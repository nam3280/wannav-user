package com.ssg.wannavapibackend.security.principal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrincipalDetails implements OAuth2User {

    private final String id;
    @Getter
    private final String provider;
    private final Map<String, Object> attributes;

    public PrincipalDetails(String id, String provider, Map<String, Object> attributes) {
        this.id = id;
        this.provider = provider;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
