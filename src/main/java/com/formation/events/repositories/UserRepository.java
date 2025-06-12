package com.formation.events.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.events.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);

  void deleteByEmail(String email);

  Optional<UserEntity> findByEmailToken(String emailToken);
}
