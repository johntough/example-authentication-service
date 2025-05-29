package com.tough.example_authentication_service.repository.integration;

import com.tough.example_authentication_service.model.User;
import com.tough.example_authentication_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("test-name");
        user.setEmail("test@email.address");
        user.setIdpSubject("test-subject");
        user.setRoles(Set.of("ROLE_USER"));
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("test-name", savedUser.getName());
        assertEquals("test@email.address", savedUser.getEmail());
        assertEquals("test-subject", savedUser.getIdpSubject());
        assertTrue(savedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testFindByGoogleSubject() {
        User user = new User();
        user.setName("test-name");
        user.setEmail("test@email.address");
        user.setIdpSubject("test-subject");
        user.setRoles(Set.of("ROLE_USER"));
        User savedUser = userRepository.save(user);

        Optional<User> returnedUser = userRepository.findByIdpSubject("test-subject");
        assertTrue(returnedUser.isPresent());
        assertNotNull(savedUser.getId());
        assertEquals("test-name", savedUser.getName());
        assertEquals("test@email.address", savedUser.getEmail());
        assertEquals("test-subject", savedUser.getIdpSubject());
        assertTrue(savedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testFindByGoogleSubjectNoUsersExist() {
        Optional<User> users = userRepository.findByIdpSubject("test-subject");
        assertTrue(users.isEmpty());
    }
}
