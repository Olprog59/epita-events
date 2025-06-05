package com.formation.events.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;

public record UserDTORegister(
    @NotBlank @Email String email,

    @NotEmpty @Pattern(regexp = "^[a-z0-9\\W]{8,64}$", flags = {
        Flag.CASE_INSENSITIVE, Flag.DOTALL }) String password,

    @NotEmpty @Pattern(regexp = "^\\p{L}{2,50}$", flags = Flag.UNICODE_CASE) String firstName,

    @NotEmpty @Pattern(regexp = "^\\p{L}{2,50}$", flags = Flag.UNICODE_CASE) String lastName){
}
