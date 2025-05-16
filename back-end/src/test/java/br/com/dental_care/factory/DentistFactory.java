package br.com.dental_care.factory;

import br.com.dental_care.dto.CreateDentistDTO;
import br.com.dental_care.dto.DentistChangePasswordDTO;
import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.dto.UpdateDentistDTO;
import br.com.dental_care.model.Dentist;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DentistFactory {

    public Dentist createValidDentist() {
        Dentist dentist = new Dentist();
        dentist.setId(1L);
        dentist.setName("Dr. John Doe");
        dentist.setEmail("john.doe@example.com");
        dentist.setPassword("#Password123");
        dentist.setPhone("(11) 99710-2376");
        dentist.setSpeciality("Orthodontics");
        dentist.setRegistrationNumber("DR12345");
        dentist.setScore(8);

        dentist.addRole(RoleFactory.createDentistRole());
        return dentist;
    }

    public DentistDTO createValidDentistDTO() {
        DentistDTO dto = DentistDTO.builder()
                .id(1L)
                .name("Dr. John Doe")
                .email("john.doe@example.com")
                .phone("(11) 99710-2376")
                .speciality("Orthodontics")
                .registrationNumber("DR12345")
                .score(8)
                .build();

        dto.addRole(RoleFactory.createDentistRoleDTO());
        dto.addSchedule(ScheduleFactory.createValidScheduleDTO());
        dto.addRating(RatingFactory.createValidRatingDTO());
        return dto;
    }

    public CreateDentistDTO createValidNewDentistDTO() {
        CreateDentistDTO dto = CreateDentistDTO.builder()
                .id(1L)
                .name("Dr. John Doe")
                .email("john.doe@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .speciality("Orthodontics")
                .registrationNumber("DR12345")
                .score(8)
                .build();

        dto.addRole(RoleFactory.createDentistRoleDTO());
        dto.addSchedule(ScheduleFactory.createValidScheduleDTO());
        dto.addRating(RatingFactory.createValidRatingDTO());
        return dto;
    }

    public DentistMinDTO createValidDentistMinDTO() {
        return DentistMinDTO.builder()
                .id(1L)
                .name("Dr. John Doe")
                .speciality("Orthodontics")
                .registrationNumber("DR12345")
                .score(8)
                .build();
    }

    public DentistMinDTO createInvalidDentistMinDTO() {
        return DentistMinDTO.builder()
                .id(999L)
                .build();
    }

    public static UpdateDentistDTO createValidUpdateDentistDTO() {
        return UpdateDentistDTO.builder()
                .id(1L)
                .name("Dr. John Doe")
                .email("john.doe@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .speciality("Orthodontics")
                .registrationNumber("DR12345")
                .score(8)
                .build();
    }

    public static DentistChangePasswordDTO createValidDentistChangePasswordDTO() {
        return DentistChangePasswordDTO.builder()
                .username("victor.dent@example.com")
                .password("#Password123")
                .newPassword("#NewPassword123")
                .confirmPassword("#NewPassword123")
                .build();
    }

}