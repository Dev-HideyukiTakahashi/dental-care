package br.com.dental_care.service;

import br.com.dental_care.dto.RoleDTO;
import br.com.dental_care.dto.UserDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.RoleMapper;
import br.com.dental_care.mapper.UserMapper;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.projection.UserDetailsProjection;
import br.com.dental_care.repository.RoleRepository;
import br.com.dental_care.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found! ID: " + id));

        logger.info("User found, id: {}", id);
        return UserMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> page = userRepository.searchAll(pageable);
        return page.map(p -> UserMapper.toDTO(p));
    }

    @Transactional
    public UserDTO save(UserDTO dto) {
        User user = new User();
        copyToEntity(user, dto);
        user = userRepository.save(user);
        logger.info("New user has been created with id: {}", user.getId());
        return UserMapper.toDTO(user);
    }

    @Transactional
    public UserDTO update(UserDTO dto, Long id) {
        try {
            User user = userRepository.getReferenceById(id);
            copyToEntity(user, dto);
            user = userRepository.save(user);
            logger.info("User updated successfully, id: {}", user.getId());
            return UserMapper.toDTO(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("User not found! ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User not found! ID: " + id);
        try {
            userRepository.deleteById(id);
            logger.info("User deleted, id: {}", id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Database error: Data integrity rules were violated.");
        }
    }

    public void copyToEntity(User user, UserDTO dto) {
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.getRoles().clear();
        checkRoles(user, dto);
    }

    private void checkRoles(User user, UserDTO dto) {
        for (RoleDTO role : dto.getRoles()) {
            if (!roleRepository.existsById(role.getId())){
                logger.warn("Attempted to create a user with a non-existent role");
                throw new ResourceNotFoundException("Role does not exist, id: " + role.getId());
            }
            user.getRoles().add(RoleMapper.toEntity(role));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> list = userRepository.searchUserAndRolesByEmail(username);
        if (list.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        User user = new User();
        user.setEmail(username);
        user.setPassword(list.get(0).getPassword());
        list.forEach(role -> user.addRole(new Role(role.getRoleId(), role.getAuthority())));

        return user;
    }
}
