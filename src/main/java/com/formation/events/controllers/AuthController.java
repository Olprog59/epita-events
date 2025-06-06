package com.formation.events.controllers;

import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.HttpsRedirectDsl;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.events.dtos.ApiResponseDTO;
import com.formation.events.dtos.users.UserLoginDTO;
import com.formation.events.dtos.users.UserMapper;
import com.formation.events.dtos.users.UserRegisterReqDTO;
import com.formation.events.dtos.users.UserRegisterRespDTO;
import com.formation.events.entities.UserEntity;
import com.formation.events.security.jwt.JwtProvider;
import com.formation.events.services.IUserService;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final IUserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;

  @PostMapping(value = "/login")
  public ResponseEntity<ApiResponseDTO> login(@Valid @RequestBody UserLoginDTO userDto) {
    try {
      final Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));

      if (authentication.isAuthenticated()) {
        String token = jwtProvider.generateToken(authentication);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(24 * 60 * 60)
            .sameSite("Lax")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(new ApiResponseDTO("200", "Bearer " + token));
      }

    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401).body(new ApiResponseDTO("401", "Les informations ne sont pas correctes"));
    }
    return ResponseEntity.badRequest().body(new ApiResponseDTO("400", "Probleme requete"));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterReqDTO userDTORegister) {
    UserEntity user = UserMapper.mapUserRegisterReqDTOToEntity(userDTORegister);
    try {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userService.inscription(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
          .body(new ApiResponseDTO(HttpStatus.NOT_ACCEPTABLE.toString(), e.getMessage()));
    }
    return ResponseEntity.ok(UserMapper.mapUserEntityToUserRegisterRespDTO(user));
  }

}
