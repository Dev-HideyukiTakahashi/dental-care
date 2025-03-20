package br.com.dental_care.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.UserMapper;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository userRepository;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);


  @Transactional(readOnly = true)
  public UserDTO findById(Long id){
    User user = userRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado! Id: " + id));
    
    logger.info("User found, id: {}", id);
    return UserMapper.toDTO(user);
  }

  @Transactional(readOnly = true)
  public Page<UserDTO> findAll(Pageable pageable){
    Page<User> page = userRepository.findAll(pageable);
    return page.map(p -> UserMapper.toDTO(p));
  }

  @Transactional
  public UserDTO save(UserDTO dto){
    User user = userRepository.save(UserMapper.toEntity(dto));
    return UserMapper.toDTO(user);
  }

  @Transactional
  public UserDTO update(UserDTO dto, Long id){  
    try{
      User user = userRepository.getReferenceById(id);
      UserMapper.copyToEntity(user, dto);
      user = userRepository.save(user);
      return UserMapper.toDTO(user);
    }catch(EntityNotFoundException e){
      throw new ResourceNotFoundException("Usuário não encontrado! Id: " + id);
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public void deleteById(Long id){
    if(!userRepository.existsById(id)) 
      throw new ResourceNotFoundException("Usuário não encontrado! Id: " + id); 
    try{
      userRepository.deleteById(id);
      logger.info("User deleted, id: {}", id);
    }catch(DataIntegrityViolationException e){
      throw new DatabaseException("Database error: Data integrity rules were violated.");
    }
  }
  

}
