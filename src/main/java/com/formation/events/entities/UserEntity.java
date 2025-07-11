package com.formation.events.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.formation.events.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Enumerated(EnumType.STRING)
  private RoleEnum role = RoleEnum.PARTICIPANT;

  @OneToMany(mappedBy = "organizer")
  private List<EventEntity> organizedEvents = new ArrayList<>();

  @ManyToMany(mappedBy = "participants")
  private List<EventEntity> participatingEvents = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private List<RegistrationEntity> registrations = new ArrayList<>();

  // mail
  @Column(length = 100)
  private String emailToken;

  @Column(nullable = false)
  private Boolean emailVerified = false;

  private LocalDateTime verificationTokenExpiredAt;

}
