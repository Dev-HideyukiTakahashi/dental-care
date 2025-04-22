package br.com.dental_care.factory;

import br.com.dental_care.dto.RoleDTO;
import br.com.dental_care.model.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleFactory {

    public RoleDTO createAdminRoleDTO() {
        return RoleDTO.builder()
                .id(1L)
                .authority("ROLE_ADMIN")
                .build();
    }

    public RoleDTO createPatientRoleDTO() {
        return RoleDTO.builder()
                .id(2L)
                .authority("ROLE_PATIENT")
                .build();
    }

    public RoleDTO createDentistRoleDTO() {
        return RoleDTO.builder()
                .id(3L)
                .authority("ROLE_DENTIST")
                .build();
    }

    public Role createAdminRole() {
        return new Role(1L, "ROLE_ADMIN");
    }

    public Role createPatientRole() {
        return new Role(2L, "ROLE_PATIENT");
    }

    public Role createDentistRole() {
        return new Role(3L, "ROLE_DENTIST");
    }
}
