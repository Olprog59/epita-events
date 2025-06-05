package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formation.events.entities.EventEntity;
import com.formation.events.entities.UserEntity;
import com.formation.events.repositories.EventRepository;
import com.formation.events.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

  private final UserRepository userRepository;

  @Override
  public List<UserEntity> getAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<UserEntity> getUserById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }

    return userRepository.findById(id);
  }

  @Override
  public Optional<UserEntity> getUserByEmail(String email) throws Exception {
    if (email.isEmpty()) {
      throw new Exception("email vide");
    }

    Optional<UserEntity> userOp = userRepository.findByEmail(email);

    if (userOp.isEmpty()) {
      throw new Exception("email non valide");
    }

    return userOp;
  }

  @Override
  public UserEntity inscription(UserEntity user) throws Exception {
    if (user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getFirstName().isEmpty()
        || user.getLastName().isEmpty()) {
      throw new Exception("données non valide");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new Exception("Utilisateur deja present");
    }

    return userRepository.save(user);
  }

  @Override
  public void deleteById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }

    userRepository.deleteById(id);
  }

  @Override
  public void deleteByEmail(String email) throws Exception {
    if (email.isEmpty()) {
      throw new Exception("email vide");
    }
    userRepository.deleteByEmail(email);
  }

}
