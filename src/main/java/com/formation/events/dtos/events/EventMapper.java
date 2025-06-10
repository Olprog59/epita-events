package com.formation.events.dtos.events;

import java.util.List;

import com.formation.events.dtos.users.UserMapper;
import com.formation.events.entities.EventEntity;

public class EventMapper {

  public static EventDTO entityToDTO(EventEntity event) {
    return new EventDTO(event.getId(), event.getTitle(), event.getDescription(), event.getStartDate(),
        event.getEndDate(), event.getLocation(), event.getMaxParticipants(),
        UserMapper.mapUserEntityToUserDTO(event.getOrganizer()),
        event.getParticipants().stream().map(UserMapper::mapUserEntityToUserDTO).toList());
  }

  public static List<EventDTO> entitiesToDTOs(List<EventEntity> events) {
    return events.stream().map(EventMapper::entityToDTO).toList();
  }
}
