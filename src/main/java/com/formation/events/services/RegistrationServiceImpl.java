package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.formation.events.entities.RegistrationEntity;
import com.formation.events.repositories.RegistrationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements IRegistrationService {

  private final RegistrationRepository registrationRepository;

  @Override
  public List<RegistrationEntity> getAll() {
    return registrationRepository.findAll();
  }

  @Override
  public Optional<RegistrationEntity> getRegistrationById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }

    return registrationRepository.findById(id);
  }

  @Override
  public RegistrationEntity save(RegistrationEntity registration) {
    return registrationRepository.save(registration);
  }

  @Override
  public void deleteById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }
    registrationRepository.deleteById(id);
  }

}
