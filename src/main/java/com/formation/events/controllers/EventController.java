package com.formation.events.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.events.dtos.events.EventDTO;
import com.formation.events.dtos.events.EventMapper;
import com.formation.events.services.IEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

  private final IEventService eventService;

  @GetMapping
  public ResponseEntity<List<EventDTO>> getAll() {
    return ResponseEntity.ok(EventMapper.entitiesToDTOs(eventService.getAll()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EventDTO> getById(@PathVariable Long id) throws Exception {
    return eventService.getEventById(id)
        .map((a) -> ResponseEntity.ok(EventMapper.entityToDTO(a)))
        .orElse(ResponseEntity.notFound().build());

  }
}
