package com.formation.events.dtos.users;

import com.formation.events.enums.RoleEnum;

public record UserDTO(
    Long id,
    String lastName,
    String firstName,
    String email,
    RoleEnum role) {
}
