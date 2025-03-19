package br.com.dental_care.mapper;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.model.User;

public class UserMapper {

  public static User toEntity(UserDTO dto){
    return new User(dto.getId(),
                    dto.getName(), 
                    dto.getEmail(), 
                    dto.getPhone(), 
                    dto.getPassword());
  }

  public static UserDTO toDTO(User user) {
    UserDTO dto = UserDTO.builder()
              .id(user.getId())
              .name(user.getName())
              .email(user.getEmail())
              .password("**********")
              .phone(user.getPhone())
              .build();

    user.getRoles().forEach(role -> dto.addRole(RoleMapper.toDTO(role)));
    return dto;
  }

  
}
