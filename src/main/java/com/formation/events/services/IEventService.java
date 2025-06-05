package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import com.formation.events.entities.EventEntity;

public interface IEventService {

  List<EventEntity> getAll();

  Optional<EventEntity> getEventById(Long id) throws Exception;

  EventEntity save(EventEntity event);

  void deleteById(Long id) throws Exception;
}
