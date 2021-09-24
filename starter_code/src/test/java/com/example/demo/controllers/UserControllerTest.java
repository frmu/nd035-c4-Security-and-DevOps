package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    private UserController sut;

    private UserRepository userRepositoryMock = mock(UserRepository.class);

    private CartRepository cartRepositoryMock = mock(CartRepository.class);

    private BCryptPasswordEncoder passwordEncoderMock = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        sut = new UserController();
        TestUtils.injectDependency(sut, "userRepository", userRepositoryMock);
        TestUtils.injectDependency(sut, "cartRepository", cartRepositoryMock);
        TestUtils.injectDependency(sut, "bCryptPasswordEncoder", passwordEncoderMock);
    }

    @Test
    public void testFindById_forExistingUser_returnsUserWithoutPassword() throws JsonProcessingException {
        long requestedUserId = 1;
        User user = new User();
        user.setId(requestedUserId);
        user.setUsername("Tester");
        user.setPassword("TesterHashedPassword");
        when(userRepositoryMock.findById(requestedUserId)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = sut.findById(requestedUserId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals("Tester", responseContent.getUsername());
        assertEquals("TesterHashedPassword", responseContent.getPassword());
        ObjectMapper mapper = new ObjectMapper();
        String responseJson = mapper.writeValueAsString(responseContent);
        assertEquals(responseJson.indexOf("TesterHashedPassword"), -1);
        assertEquals(requestedUserId, responseContent.getId());
        
    }

    @Test
    public void testFindById_forNonExistingUser_returnsNotFound() {
        when(userRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        final ResponseEntity<User> response = sut.findById(99L);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findByUserName_forExistingUser_returnsUser() {
        String requestedUserName = "Tester";
        User user = new User();
        user.setId(1);
        user.setUsername(requestedUserName);
        user.setPassword("TesterHashedPassword");
        when(userRepositoryMock.findByUsername(requestedUserName)).thenReturn(user);

        final ResponseEntity<User> response = sut.findByUserName(requestedUserName);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(requestedUserName, responseContent.getUsername());
        assertEquals(1, responseContent.getId());
    }

    @Test
    public void createUser_forValidInput_createsUserSuccessfully() {
        CreateUserRequest inputRequest = new CreateUserRequest();
        inputRequest.setUsername("Tester");
        inputRequest.setPassword("TesterCleartextPassword");
        inputRequest.setConfirmPassword("TesterCleartextPassword");
        when(passwordEncoderMock.encode("TesterCleartextPassword")).thenReturn("TesterHashedPassword");
        Cart cart = new Cart();
        when(cartRepositoryMock.save(cart)).thenReturn(cart);

        final ResponseEntity<User> response = sut.createUser(inputRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals("Tester", responseContent.getUsername());
        assertEquals("TesterHashedPassword", responseContent.getPassword());
        assertEquals(0, responseContent.getId());
    }
    
}
