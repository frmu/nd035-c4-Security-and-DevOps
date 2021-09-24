package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {

    OrderController sut = new OrderController();

    private UserRepository userRepositoryMock = mock(UserRepository.class);
	
	private OrderRepository orderRepositoryMock = mock(OrderRepository.class);

    @Before
    public void setup() {
        sut = new OrderController();
        TestUtils.injectDependency(sut, "userRepository", userRepositoryMock);
        TestUtils.injectDependency(sut, "orderRepository", orderRepositoryMock);
    }
    
    @Test
    public void getOrdersForUser_forValidUser_returnsOrders() {
        String input = "Tester";
        User user = new User();
        user.setUsername(input);
        when(userRepositoryMock.findByUsername(input)).thenReturn(user);
        UserOrder order = new UserOrder();
        order.setUser(user);
        when(orderRepositoryMock.findByUser(user)).thenReturn(List.of(order));

        final ResponseEntity<List<UserOrder>> response = sut.getOrdersForUser(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(input, responseContent.get(0).getUser().getUsername());
    }

    @Test
    public void getOrdersForUser_forInvalidUser_returnsNotFound() {
        String input = "Tester";
        when(userRepositoryMock.findByUsername(input)).thenReturn(null);
        
        final ResponseEntity<List<UserOrder>> response = sut.getOrdersForUser(input);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<UserOrder> responseContent = response.getBody();
        assertNull(responseContent);
    }

    @Test
    public void submit_forValidUserWithCart_returnsOrder() {
        String input = "Tester";
        User user = new User();
        user.setUsername(input);
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2));
        cart.addItem(item);
        cart.addItem(item);
        when(userRepositoryMock.findByUsername(input)).thenReturn(user);
        UserOrder order = new UserOrder();
        when(orderRepositoryMock.save(order)).thenReturn(order);

        final ResponseEntity<UserOrder> response = sut.submit(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(input, responseContent.getUser().getUsername());
        assertEquals(2, responseContent.getItems().size());
        assertEquals(4, responseContent.getTotal().intValue());
    }

    @Test
    public void submit_forInvalidUser_returnsNotFound() {
        String input = "Tester";
        when(userRepositoryMock.findByUsername(input)).thenReturn(null);
        
        final ResponseEntity<UserOrder> response = sut.submit(input);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        UserOrder responseContent = response.getBody();
        assertNull(responseContent);
    }
}
