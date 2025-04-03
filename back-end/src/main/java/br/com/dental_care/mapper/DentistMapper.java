package br.com.dental_care.mapper;

import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.model.Dentist;

public class DentistMapper {
    public static DentistDTO toDTO(Dentist entity) {

        DentistDTO dto = DentistDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password("**********")
                .phone(entity.getPhone())
                .speciality(entity.getSpeciality())
                .registrationNumber(entity.getRegistrationNumber())
                .score(entity.getScore())
                .build();

        entity.getSchedules().forEach(schedule ->
                dto.addSchedule(ScheduleMapper.toDTO(schedule)));
        entity.getRatings().forEach(rating ->
                dto.addRating(RatingMapper.toDTO(rating)));
        entity.getRoles().forEach(role -> dto.addRole(RoleMapper.toDTO(role)));
        return dto;
    }

    public static DentistMinDTO toMinDTO(Dentist entity) {

        DentistMinDTO dto = DentistMinDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .speciality(entity.getSpeciality())
                .registrationNumber(entity.getRegistrationNumber())
                .score(entity.getScore())
                .build();
        return dto;
    }
}
