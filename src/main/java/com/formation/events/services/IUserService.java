package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import com.formation.events.entities.UserEntity;

public interface IUserService {

  List<UserEntity> getAll();

  Optional<UserEntity> getUserById(Long id) throws Exception;

  Optional<UserEntity> getUserByEmail(String email) throws Exception;

  UserEntity inscription(UserEntity user) throws Exception;

  void deleteById(Long id) throws Exception;

  void deleteByEmail(String email) throws Exception;

}
