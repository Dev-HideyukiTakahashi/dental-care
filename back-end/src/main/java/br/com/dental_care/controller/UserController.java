package br.com.dental_care.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping(path = "/{id}")
  public ResponseEntity<UserDTO> findById(@PathVariable Long id){
    logger.info("Searching user with id: {}", id);
    UserDTO dto = userService.findById(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping
  public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){
    logger.info("Searching all users");
    Page<UserDTO> dto = userService.findAll(pageable);
    return ResponseEntity.ok(dto);
  }
  
}
