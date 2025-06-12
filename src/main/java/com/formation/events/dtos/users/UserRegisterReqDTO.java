package com.formation.events.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;

public record UserRegisterReqDTO(
    @NotBlank @Email String email,

    @NotEmpty @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,64}$", message = "{user.dto.password.pattern}") String password,

    @NotEmpty @Pattern(regexp = "^\\p{L}{2,50}$", flags = Flag.UNICODE_CASE) String firstName,

    @NotEmpty @Pattern(regexp = "^\\p{L}{2,50}$", flags = Flag.UNICODE_CASE)

    String lastName) {
}
