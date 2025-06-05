package com.formation.events.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.formation.events.entities.UserEntity;
import com.formation.events.enums.RoleEnum;
import com.formation.events.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;

  @Override
  public void run(String... args) {
    // Créer des utilisateurs de test
    UserEntity organizer = new UserEntity();
    organizer.setEmail("organizer@test.com");
    organizer.setPassword("password123");
    organizer.setFirstName("Jean");
    organizer.setLastName("Organisateur");
    organizer.setRole(RoleEnum.ORGANIZER);
    userRepository.save(organizer);

    System.out.println("Données de test initialisées !");
  }
}
