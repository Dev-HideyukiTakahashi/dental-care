package br.com.dental_care.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.service.UserService;
import jakarta.validation.Valid;
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

  @PostMapping
  public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserDTO dto){
    logger.info("Creating new user with email: {}", dto.getEmail());
    dto = userService.save(dto);
    URI uri = ServletUriComponentsBuilder
              .fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(dto.getId())
              .toUri();
    return ResponseEntity.created(uri).body(dto);
  }

  @PutMapping(path= "/{id}")
  public ResponseEntity<UserDTO> update(@Valid @RequestBody UserDTO dto, @PathVariable Long id){
    logger.info("Updating user with id: {}, email: {}", id, dto.getEmail());
    dto = userService.update(dto,id);
    return ResponseEntity.ok(dto);
  }
  
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id){
    logger.info("Deleting user with id: {}", id);
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
