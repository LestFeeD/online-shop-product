package com.shopir.product.security;

import com.shopir.product.dto.UserKafkaDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MyUserDetails implements UserDetails {

    private Long idUser;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;

    public MyUserDetails(Long idUser, String email, String password, Collection<GrantedAuthority> authorities) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }
    public static MyUserDetails buildUserDetails(UserKafkaDto user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new MyUserDetails(
                user.getIdUser(),
                user.getEmail(),
                user.getPassword(),
                authorities);

    }

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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
