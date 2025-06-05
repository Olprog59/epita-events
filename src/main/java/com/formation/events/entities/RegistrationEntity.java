package com.formation.events.entities;

import java.time.LocalDateTime;

import com.formation.events.enums.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "registration")
public class RegistrationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime registrationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusEnum status = StatusEnum.PENDING;

  @ManyToOne
  private UserEntity user;

  @ManyToOne
  private EventEntity event;
}
