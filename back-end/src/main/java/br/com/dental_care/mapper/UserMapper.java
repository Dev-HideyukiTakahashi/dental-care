package br.com.dental_care.mapper;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.model.User;

public class UserMapper {

  public static User toEntity(UserDTO dto){
      User user = new User(dto.getId(),
                    dto.getName(), 
                    dto.getEmail(), 
                    dto.getPhone(), 
                    dto.getPassword());

      dto.getRoles().forEach(role -> user.getRoles().add(RoleMapper.toEntity(role)));

      return user;
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

  public static void copyToEntity(User user, UserDTO dto) {
    user.setName(dto.getName());
    user.setPassword(dto.getPassword());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.getRoles().clear();

    dto.getRoles().forEach(role -> user.getRoles().add(RoleMapper.toEntity(role)));
  }

  
}
