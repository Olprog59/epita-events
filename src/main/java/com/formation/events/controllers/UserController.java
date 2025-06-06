package com.formation.events.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.events.dtos.users.UserRegisterReqDTO;
import com.formation.events.dtos.users.UserRegisterRespDTO;
import com.formation.events.dtos.users.UserMapper;
import com.formation.events.entities.UserEntity;
import com.formation.events.repositories.UserRepository;
import com.formation.events.services.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final IUserService userService;

  @GetMapping
  public ResponseEntity<List<UserEntity>> getAll() {
    return ResponseEntity.ok(userService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserEntity> getUser(@PathVariable Long id) throws Exception {
    /*
     * return userService.getUserById(id)
     * .map(ResponseEntity::ok)
     * .orElse(ResponseEntity.notFound().build());
     */

    Optional<UserEntity> userOp = userService.getUserById(id);
    if (userOp.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(userOp.get());

  }

}
