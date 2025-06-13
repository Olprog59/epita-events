package com.formation.events.controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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

  private final Bucket loginBucket = Bucket.builder()
      .addLimit(limit -> limit.capacity(5).refillIntervally(1, Duration.ofSeconds(15)))
      .build();

  // [T T T T T]
  // [T T T T]
  // [T T T]
  // [T T]
  // [T]
  // [] => false (rate limit ok)
  @PostMapping(value = "/login")
  public ResponseEntity<ApiResponseDTO> login(@Valid @RequestBody UserLoginDTO userDto) {
    try {
      if (!loginBucket.tryConsume(1)) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ApiResponseDTO(
            HttpStatus.TOO_MANY_REQUESTS.toString(), "Trop de tentatives, réessayer dans quelques secondes"));
      }
      final Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));

      if (authentication.isAuthenticated()) {

        UserEntity user = userService.getUserByEmail(userDto.email()).orElse(null);

        if (user != null && !user.getEmailVerified()) {
          return ResponseEntity.status(401)
              .body(new ApiResponseDTO("401", "Email non vérifié. Vérifiez dans votre boite mail."));
        }

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

    } catch (Exception e) {
      return ResponseEntity.status(401).body(new ApiResponseDTO("401", "Les informations ne sont pas correctes"));
    }
    return ResponseEntity.badRequest().body(new ApiResponseDTO("400", "Probleme requete"));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterReqDTO userDTORegister) {
    UserEntity user = UserMapper.mapUserRegisterReqDTOToEntity(userDTORegister);
    try {
      user.setPassword(passwordEncoder.encode(user.getPassword()));

      // generation du token email pour l'envoi d'email
      userService.generateVerificationToken(user);

      // sauvegarde de l'Utilisateur dans la BDD
      userService.inscription(user);

      // envoi de l'email
      emailService.sendVerificationEmail(user.getEmail(), user.getEmailToken());

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

  /*
   * @GetMapping("/test-email")
   * public ResponseEntity<?> testSendEmail() {
   * UUID uuid = UUID.randomUUID();
   * emailService.sendVerificationEmail("samuel.michaux@gmail.com",
   * uuid.toString());
   * return ResponseEntity.ok().build();
   * }
   */

  @GetMapping("/verify-email/{token}")
  public ResponseEntity<?> verifyEmail(@PathVariable String token) {
    try {
      Optional<UserEntity> userOp = userService.getUserByEmailToken(token);

      if (userOp.isEmpty()) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
            .body(new ApiResponseDTO(HttpStatus.I_AM_A_TEAPOT.toString(), "Utilisateur non trouvé"));
      }

      UserEntity user = userOp.get();

      if (user.getEmailVerified()) {
        return ResponseEntity.ok(new ApiResponseDTO("200", "Email déjà vérifié"));
      }

      if (user.getVerificationTokenExpiredAt() != null
          && user.getVerificationTokenExpiredAt().isBefore(LocalDateTime.now())) {
        userService.deleteById(user.getId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO("400", "Email Token expiré"));
      }

      user.setEmailVerified(true);
      user.setEmailToken(null);
      user.setVerificationTokenExpiredAt(null);
      userService.update(user);

      return ResponseEntity.ok(new ApiResponseDTO("200", "Email vérifié avec succès"));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponseDTO("500", "Erreur: " + e.getMessage()));
    }
  }
}
