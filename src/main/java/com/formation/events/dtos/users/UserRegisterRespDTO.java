package com.formation.events.dtos.users;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRegisterRespDTO(
    String email,
    String firstName,
    String lastName) {
}
