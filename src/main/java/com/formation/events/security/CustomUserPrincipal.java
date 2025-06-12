package com.formation.events.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.formation.events.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

  private Long id;
  private String email;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  public static CustomUserPrincipal create(UserEntity user) {
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    return new CustomUserPrincipal(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        authorities);
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

}
