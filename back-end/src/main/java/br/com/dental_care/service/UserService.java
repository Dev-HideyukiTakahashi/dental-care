package br.com.dental_care.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.UserMapper;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository userRepository;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);


  @Transactional(readOnly = true)
  public UserDTO findById(Long id){
    User user = userRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    
    logger.info("User found, id: {}", id);
    return UserMapper.toDTO(user);
  }
}
