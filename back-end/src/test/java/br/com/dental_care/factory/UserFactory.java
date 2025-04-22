package br.com.dental_care.factory;

import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFactory {

    public User createValidUser() {
        Role role = RoleFactory.createPatientRole();

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("11912345678");
        user.setPassword("encoded-password");
        user.getRoles().add(role);

        return user;
    }
}
