package fr.epitech.cash.manager.service;


import fr.epitech.project.manager.dto.UserDto;
import fr.epitech.project.manager.service.UserService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateUser()
    {
        // Given
        String email = "rdla@hotmail.fr";
        String password = "toto";

        // When
        UserDto userDto = userService.createUser(email, password);
        // Then
        Assertions.assertEquals(userDto.getEmail(), email);
        Assertions.assertNotNull(userDto.getId());
    }

}
