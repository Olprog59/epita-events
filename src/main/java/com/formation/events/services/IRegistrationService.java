package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import com.formation.events.entities.RegistrationEntity;

public interface IRegistrationService {

  List<RegistrationEntity> getAll();

  Optional<RegistrationEntity> getRegistrationById(Long id) throws Exception;

  RegistrationEntity save(RegistrationEntity registration);

  void deleteById(Long id) throws Exception;
}
