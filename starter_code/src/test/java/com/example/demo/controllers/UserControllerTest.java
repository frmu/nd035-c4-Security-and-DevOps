package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class UserControllerTest {

    private UserController sut;

    private UserRepository userRepositoryMock = mock(UserRepository.class);

    @Before
    public void setup() {
        sut = new UserController();
        TestUtils.injectDependency(sut, "userRepository", userRepositoryMock);
    }

    @Test
    public void testFindById() {
        long requestedUserId = 1;
        User user = new User();
        user.setId(requestedUserId);
        user.setUsername("Tester");
        user.setPassword("TesterPassword");
        when(userRepositoryMock.findById(requestedUserId)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = sut.findById(requestedUserId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals("Tester", responseContent.getUsername());
        assertEquals(requestedUserId, responseContent.getId());
    }
    
}
