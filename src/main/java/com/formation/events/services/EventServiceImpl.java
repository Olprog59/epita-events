package com.formation.events.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.formation.events.entities.EventEntity;
import com.formation.events.repositories.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements IEventService {

  private final EventRepository eventRepository;

  @Override
  public List<EventEntity> getAll() {
    return eventRepository.findAll();
  }

  @Override
  public Optional<EventEntity> getEventById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }
    return eventRepository.findById(id);
  }

  @Override
  public EventEntity save(EventEntity event) {
    return eventRepository.save(event);
  }

  @Override
  public void deleteById(Long id) throws Exception {
    if (id == null || id <= 0) {
      throw new Exception("id non correct");
    }
    eventRepository.deleteById(id);
  }

}
