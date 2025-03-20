package br.com.dental_care.service;

import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.dto.RoleDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.DentistMapper;
import br.com.dental_care.mapper.RoleMapper;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DentistService {

    private final DentistRepository dentistRepository;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(DentistService.class);


    @Transactional(readOnly = true)
    public DentistDTO findById(Long id) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found! ID: " + id));

        logger.info("Dentist found, id: {}", id);
        return DentistMapper.toDTO(dentist);
    }

    @Transactional(readOnly = true)
    public Page<DentistMinDTO> findAll(Pageable pageable) {
        Page<Dentist> page = dentistRepository.findAll(pageable);
        return page.map(p -> DentistMapper.toMinDTO(p));
    }

    @Transactional
    public DentistDTO save(DentistDTO dto) {
        Dentist dentist = new Dentist();
        copyToEntity(dentist, dto);
        dentist = dentistRepository.save(dentist);
        logger.info("New dentist has been created with id: {}", dentist.getId());
        return DentistMapper.toDTO(dentist);
    }

    @Transactional
    public DentistDTO update(DentistDTO dto, Long id) {
        try {
            Dentist dentist = dentistRepository.getReferenceById(id);
            copyToEntity(dentist, dto);
            dentist = dentistRepository.save(dentist);
            logger.info("Dentist updated successfully, id: {}", dentist.getId());
            return DentistMapper.toDTO(dentist);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Dentist not found! ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!dentistRepository.existsById(id))
            throw new ResourceNotFoundException("Dentist not found! ID: " + id);
        try {
            dentistRepository.deleteById(id);
            logger.info("Dentist deleted, id: {}", id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Database error: Data integrity rules were violated.");
        }
    }

    public void copyToEntity(Dentist dentist, DentistDTO dto) {
        dentist.setName(dto.getName());
        dentist.setPassword(dto.getPassword());
        dentist.setEmail(dto.getEmail());
        dentist.setPhone(dto.getPhone());
        dentist.setRegistrationNumber(dto.getRegistrationNumber());
        dentist.setSpeciality(dto.getSpeciality());
        dentist.getRoles().clear();
        checkRoles(dentist, dto);
    }

    private void checkRoles(Dentist dentist, DentistDTO dto) {
        for (RoleDTO role : dto.getRoles()) {
            if (!roleRepository.existsById(role.getId())){
                logger.warn("Attempted to create a dentist with a non-existent role");
                throw new ResourceNotFoundException("Role does not exist, id: " + role.getId());
            }
            dentist.getRoles().add(RoleMapper.toEntity(role));
        }
    }
}
