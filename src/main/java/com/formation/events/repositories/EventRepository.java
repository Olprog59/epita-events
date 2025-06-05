package com.formation.events.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.events.entities.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

}
