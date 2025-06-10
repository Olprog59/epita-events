package com.formation.events.dtos.users;

import java.io.ObjectInputStream.GetField;

import com.formation.events.entities.UserEntity;

public class UserMapper {

  public static UserEntity mapUserRegisterReqDTOToEntity(UserRegisterReqDTO userDTO) {
    UserEntity user = new UserEntity();
    user.setEmail(userDTO.email());
    user.setPassword(userDTO.password());
    user.setFirstName(userDTO.firstName());
    user.setLastName(userDTO.lastName());

    return user;
  }

  public static UserRegisterRespDTO mapUserEntityToUserRegisterRespDTO(UserEntity user) {
    return new UserRegisterRespDTO(user.getEmail(), user.getFirstName(), user.getLastName());
  }

  public static UserEntity mapUserLoginDTOToEntity(UserLoginDTO userDTO) {
    UserEntity user = new UserEntity();
    user.setEmail(userDTO.email());
    user.setPassword(userDTO.password());

    return user;
  }

  public static UserDTO mapUserEntityToUserDTO(UserEntity user) {
    return new UserDTO(user.getId(), user.getLastName(), user.getFirstName(), user.getEmail(), user.getRole());
  }
}
