package br.com.dental_care.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientDTO {

    private Long id;
    private String medicalHistory;
    private String name;
    private String email;
    private String phone;

    private final List<RoleDTO> roles = new ArrayList<>();

    private final List<AppointmentMinDTO> appointments = new ArrayList<>();

    public void addAppointment(AppointmentMinDTO appointment) {
        appointments.add(appointment);
    }

    public void addRole(RoleDTO role) {
        roles.add(role);
    }

}
