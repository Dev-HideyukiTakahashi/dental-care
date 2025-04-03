package br.com.dental_care.service;

import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.projection.UserDetailsProjection;
import br.com.dental_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
