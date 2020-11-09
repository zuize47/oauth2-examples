package dev.sultanov.springboot.oauth2.mfa.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class CustomUser extends org.springframework.security.core.userdetails.User {
    private long userId;

    public CustomUser(long userId, String username, String password, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomUser that = (CustomUser) o;
        return userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}
