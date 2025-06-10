package com.formation.events.dtos.events;

import java.time.LocalDateTime;
import java.util.List;

import com.formation.events.dtos.users.UserDTO;

public record EventDTO(
    Long id,
    String title,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String location,
    Integer maxParticipants,
    UserDTO organizer,
    List<UserDTO> participants) {

}
