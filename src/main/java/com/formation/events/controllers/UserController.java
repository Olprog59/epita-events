package com.formation.events.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.events.entities.UserEntity;
import com.formation.events.services.IUserService;

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
