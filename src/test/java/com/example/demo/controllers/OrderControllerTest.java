package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Cart cart;
    private UserOrder order1;
    private UserOrder order2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create a user and a cart
        user = new User();
        user.setUsername("testUser");
        cart = new Cart();
        user.setCart(cart);

        // Create two orders
        order1 = new UserOrder();
        order1.setId(1L);
        order1.setUser(user);
        order1.setTotal(BigDecimal.valueOf(100));

        order2 = new UserOrder();
        order2.setId(2L);
        order2.setUser(user);
        order2.setTotal(BigDecimal.valueOf(200));
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(orderRepository, times(0)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmitOrder_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(order1);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(orderRepository, times(0)).findByUser(any(User.class));
    }

    @Test
    public void testGetOrdersForUser_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order1, order2));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(orderRepository, times(1)).findByUser(user);
    }
}