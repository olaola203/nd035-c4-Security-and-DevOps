package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;
    private Cart cart;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cart = new Cart();
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setCart(cart);
    }

    @Test
    public void testFindById_UserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByUserName_UserFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    public void testFindByUserName_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    public void testCreateUser_Success() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("wrongPassword");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordTooShort() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("short");
        createUserRequest.setConfirmPassword("short");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(0)).save(any(User.class));
    }
}