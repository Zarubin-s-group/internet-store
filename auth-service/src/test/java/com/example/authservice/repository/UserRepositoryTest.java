package com.example.authservice.repository;

import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepositoryJpa;

    @Test
    public void whenGetByName_thenReturnUser() {
        Role role = new Role("ROLE_USER");
        entityManager.persist(role);

        User user = new User(
                "Petrov",
                "password",
                Collections.singletonList(role)
        );
        entityManager.persist(user);
        entityManager.flush();

        String userName = user.getName();
        User gotUser = userRepositoryJpa.findByName(userName).get();

        assertThat(gotUser.getPassword())
                .isEqualTo(user.getPassword());
    }
}
