package com.formation.events.config;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.formation.events.entities.EventEntity;
import com.formation.events.entities.RegistrationEntity;
import com.formation.events.entities.UserEntity;
import com.formation.events.enums.RoleEnum;
import com.formation.events.enums.StatusEnum;
import com.formation.events.repositories.EventRepository;
import com.formation.events.repositories.RegistrationRepository;
import com.formation.events.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
  private final UserRepository userRepository;
  private final EventRepository eventRepository;
  private final RegistrationRepository registrationRepository;

  @Override
  public void run(String... args) {
    // Vérifier si les données existent déjà pour éviter les doublons
    if (userRepository.count() > 0) {
      System.out.println("Les données existent déjà, initialisation ignorée.");
      return;
    }

    // Créer des organisateurs
    UserEntity organizer1 = createUser(
        "organizer1@test.com",
        "password123",
        "Jean",
        "Organisateur",
        RoleEnum.ORGANIZER);

    UserEntity organizer2 = createUser(
        "organizer2@test.com",
        "password123",
        "Marie",
        "Dupont",
        RoleEnum.ORGANIZER);

    // Créer des participants
    UserEntity participant1 = createUser(
        "participant1@test.com",
        "password123",
        "Pierre",
        "Martin",
        RoleEnum.PARTICIPANT);

    UserEntity participant2 = createUser(
        "participant2@test.com",
        "password123",
        "Sophie",
        "Bernard",
        RoleEnum.PARTICIPANT);

    UserEntity participant3 = createUser(
        "participant3@test.com",
        "password123",
        "Luc",
        "Moreau",
        RoleEnum.PARTICIPANT);

    UserEntity participant4 = createUser(
        "participant4@test.com",
        "password123",
        "Emma",
        "Leroy",
        RoleEnum.PARTICIPANT);

    // Sauvegarder tous les utilisateurs
    userRepository.saveAll(Arrays.asList(
        organizer1, organizer2, participant1, participant2, participant3, participant4));

    // Créer des événements
    EventEntity conference = createEvent(
        "Conférence Tech 2025",
        "Grande conférence sur les nouvelles technologies et l'innovation digitale",
        LocalDateTime.of(2025, 7, 15, 9, 0),
        LocalDateTime.of(2025, 7, 15, 17, 0),
        "Centre de Conférences de Paris",
        100,
        organizer1);

    EventEntity workshop = createEvent(
        "Workshop Spring Boot",
        "Atelier pratique pour apprendre Spring Boot de A à Z",
        LocalDateTime.of(2025, 6, 20, 14, 0),
        LocalDateTime.of(2025, 6, 20, 18, 0),
        "Salle de formation Tech Academy",
        25,
        organizer2);

    EventEntity meetup = createEvent(
        "Meetup Java Developers",
        "Rencontre mensuelle des développeurs Java de la région",
        LocalDateTime.of(2025, 6, 10, 19, 0),
        LocalDateTime.of(2025, 6, 10, 22, 0),
        "Café des Développeurs",
        50,
        organizer1);

    EventEntity formation = createEvent(
        "Formation Docker & Kubernetes",
        "Formation intensive sur la containerisation et l'orchestration",
        LocalDateTime.of(2025, 8, 5, 9, 0),
        LocalDateTime.of(2025, 8, 7, 17, 0),
        "Centre de Formation Avancée",
        15,
        organizer2);

    // Sauvegarder tous les événements
    eventRepository.saveAll(Arrays.asList(conference, workshop, meetup, formation));

    // Créer des inscriptions
    createRegistration(participant1, conference, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(10));
    createRegistration(participant2, conference, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(8));
    createRegistration(participant3, conference, StatusEnum.PENDING, LocalDateTime.now().minusDays(3));

    createRegistration(participant1, workshop, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(5));
    createRegistration(participant4, workshop, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(2));

    createRegistration(participant2, meetup, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(7));
    createRegistration(participant3, meetup, StatusEnum.CONFIRMED, LocalDateTime.now().minusDays(4));
    createRegistration(participant4, meetup, StatusEnum.PENDING, LocalDateTime.now().minusDays(1));

    createRegistration(participant1, formation, StatusEnum.PENDING, LocalDateTime.now().minusHours(2));
    createRegistration(participant2, formation, StatusEnum.CANCELLED, LocalDateTime.now().minusDays(6));

    // Ajouter quelques participants directement aux événements (pour tester la
    // relation ManyToMany)
    conference.getParticipants().addAll(Arrays.asList(participant1, participant2));
    workshop.getParticipants().addAll(Arrays.asList(participant1, participant4));
    meetup.getParticipants().addAll(Arrays.asList(participant2, participant3));

    // Sauvegarder les modifications
    eventRepository.saveAll(Arrays.asList(conference, workshop, meetup, formation));

    System.out.println("=== Données de test initialisées avec succès ! ===");
    System.out.println("Utilisateurs créés : " + userRepository.count());
    System.out.println("Événements créés : " + eventRepository.count());
    System.out.println("Inscriptions créées : " + registrationRepository.count());
    System.out.println("=========================================");
  }

  private UserEntity createUser(String email, String password, String firstName, String lastName, RoleEnum role) {
    UserEntity user = new UserEntity();
    user.setEmail(email);
    user.setPassword(password);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setRole(role);
    return user;
  }

  private EventEntity createEvent(String title, String description, LocalDateTime startDate,
      LocalDateTime endDate, String location, Integer maxParticipants,
      UserEntity organizer) {
    EventEntity event = new EventEntity();
    event.setTitle(title);
    event.setDescription(description);
    event.setStartDate(startDate);
    event.setEndDate(endDate);
    event.setLocation(location);
    event.setMaxParticipants(maxParticipants);
    event.setOrganizer(organizer);
    return event;
  }

  private void createRegistration(UserEntity user, EventEntity event, StatusEnum status,
      LocalDateTime registrationDate) {
    RegistrationEntity registration = new RegistrationEntity();
    registration.setUser(user);
    registration.setEvent(event);
    registration.setStatus(status);
    registration.setRegistrationDate(registrationDate);
    registrationRepository.save(registration);
  }
}
