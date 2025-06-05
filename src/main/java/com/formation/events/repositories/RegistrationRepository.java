package com.formation.events.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.events.entities.RegistrationEntity;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

}
