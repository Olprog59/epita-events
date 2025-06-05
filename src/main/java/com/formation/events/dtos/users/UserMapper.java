package com.formation.events.dtos.users;

import com.formation.events.entities.UserEntity;

public class UserMapper {

  public static UserEntity userMapperDtoRegister(UserDTORegister userDTO) {
    UserEntity user = new UserEntity();
    user.setEmail(userDTO.email());
    user.setPassword(userDTO.password());
    user.setFirstName(userDTO.firstName());
    user.setLastName(userDTO.lastName());

    return user;
  }

}
