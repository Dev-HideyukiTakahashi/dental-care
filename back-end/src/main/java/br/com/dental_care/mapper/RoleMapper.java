package br.com.dental_care.mapper;

import br.com.dental_care.dto.RoleDTO;
import br.com.dental_care.model.Role;

public class RoleMapper {

  public static Role toEntity(RoleDTO dto) {
    return new Role(dto.getId(), dto.getAuthority());
  }

  public static RoleDTO toDTO(Role role) {
    return RoleDTO.builder()
          .id(role.getId())
          .authority(role.getAuthority())
          .build();
  }
}
