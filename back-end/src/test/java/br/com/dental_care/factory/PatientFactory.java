package br.com.dental_care.factory;

import br.com.dental_care.dto.CreatePatientDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.dto.UpdatePatientDTO;
import br.com.dental_care.model.Patient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PatientFactory {

    public PatientDTO createValidPatientDTO() {
        PatientDTO dto = PatientDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("(11) 99710-2376")
                .medicalHistory("No known allergies")
                .build();
        dto.addRole(RoleFactory.createPatientRoleDTO());
        dto.addAppointment(AppointmentFactory.createValidAppointmentMinDTO());

        return dto;
    }

    public CreatePatientDTO createValidNewPatientDTO() {
        CreatePatientDTO dto = CreatePatientDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .medicalHistory("No known allergies")
                .build();
        dto.addRole(RoleFactory.createPatientRoleDTO());
        dto.addAppointment(AppointmentFactory.createValidAppointmentMinDTO());

        return dto;
    }

    public UpdatePatientDTO createValidUpdatePatientDTO() {
        UpdatePatientDTO dto = UpdatePatientDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .medicalHistory("No known allergies")
                .build();

        return dto;
    }

    public PatientMinDTO createValidPatientMinDTO() {
        return PatientMinDTO.builder()
                .id(1L)
                .name("John Doe")
                .medicalHistory("No known allergies")
                .phone("(11) 99710-2376")
                .build();
    }

    public Patient createValidPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPassword("#Password123");
        patient.setPhone("(11) 99710-2376");
        patient.setMedicalHistory("No known allergies");

        patient.addRole(RoleFactory.createPatientRole());

        return patient;
    }

    public static PatientMinDTO createInvalidPatientMinDTO() {
        return createValidPatientMinDTO()
                .toBuilder()
                .id(999L)
                .build();
    }
}
