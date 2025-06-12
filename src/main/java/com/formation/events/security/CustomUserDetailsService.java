package com.formation.events.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.formation.events.entities.UserEntity;
import com.formation.events.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu avec cet email : " + username));

    return CustomUserPrincipal.create(user);
  }

}
