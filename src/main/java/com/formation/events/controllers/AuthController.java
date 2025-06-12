package com.formation.events.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.events.dtos.ApiResponseDTO;
import com.formation.events.dtos.users.UserLoginDTO;
import com.formation.events.dtos.users.UserMapper;
import com.formation.events.dtos.users.UserRegisterReqDTO;
import com.formation.events.entities.UserEntity;
import com.formation.events.security.CustomUserPrincipal;
import com.formation.events.security.jwt.JwtProvider;
import com.formation.events.services.IEmailService;
import com.formation.events.services.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final IUserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final IEmailService emailService;

  @Value("${app.cookie.name:jwt_token}")
  private String cookieName;

  @PostMapping(value = "/login")
  public ResponseEntity<ApiResponseDTO> login(@Valid @RequestBody UserLoginDTO userDto) {
    try {
      final Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));

      if (authentication.isAuthenticated()) {
        String token = jwtProvider.generateToken(authentication);

        ResponseCookie jwtCookie = ResponseCookie.from(cookieName, token)
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

  @GetMapping("/me")
  public ResponseEntity<ApiResponseDTO> getCurrentUser(@AuthenticationPrincipal CustomUserPrincipal currentUser) {
    if (currentUser == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO("401", "Token invalide ou expiré"));
    }

    return ResponseEntity.ok(new ApiResponseDTO("200", "Utilisateur OK"));
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout() {
    ResponseCookie deleteCookie = ResponseCookie.from(cookieName, "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new ApiResponseDTO("200", "Déconnexion OK"));
  }

  @GetMapping("/test-email")
  public ResponseEntity<?> testSendEmail() {
    UUID uuid = UUID.randomUUID();
    emailService.sendVerificationEmail("samuel.michaux@gmail.com", uuid.toString());
    return ResponseEntity.ok().build();
  }
}
