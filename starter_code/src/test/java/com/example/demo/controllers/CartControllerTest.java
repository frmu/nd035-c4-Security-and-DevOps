package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CartControllerTest {

    private CartController sut;

    private UserRepository userRepositoryMock = mock(UserRepository.class);
	
	private CartRepository cartRepositoryMock = mock(CartRepository.class);
	
	private ItemRepository itemRepositoryMock = mock(ItemRepository.class);

    @Before
    public void setup() {
        sut = new CartController();
        TestUtils.injectDependency(sut, "userRepository", userRepositoryMock);
        TestUtils.injectDependency(sut, "cartRepository", cartRepositoryMock);
        TestUtils.injectDependency(sut, "itemRepository", itemRepositoryMock);
    }

    @Test
    public void testAddTocart_withValidInput_returnsFilledCart() {
        ModifyCartRequest input = new ModifyCartRequest();
        input.setItemId(1);
        input.setQuantity(2);
        input.setUsername("Tester");
        User user = new User();
        user.setUsername("Tester");
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2));

        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.of(item));
        when(userRepositoryMock.findByUsername("Tester")).thenReturn(user);
        when(cartRepositoryMock.save(cart)).thenReturn(cart);

        final ResponseEntity<Cart> response = sut.addTocart(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(2, responseContent.getItems().size());
        assertEquals(4, responseContent.getTotal().intValue());
        assertEquals("Tester", responseContent.getUser().getUsername());
    }

    @Test
    public void testAddTocart_forInvalidUser_returnsNotFound() {
        ModifyCartRequest input = new ModifyCartRequest();
        input.setUsername("Tester");
        User user = new User();
        user.setUsername("Tester");
        
        when(userRepositoryMock.findByUsername("Tester")).thenReturn(null);
        
        final ResponseEntity<Cart> response = sut.addTocart(input);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRemoveFromcart_withValidInput_returnsCartWithLessItems() {
        ModifyCartRequest input = new ModifyCartRequest();
        input.setItemId(1);
        input.setQuantity(1);
        input.setUsername("Tester");
        User user = new User();
        user.setUsername("Tester");
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2));
        cart.addItem(item);
        cart.addItem(item);

        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.of(item));
        when(userRepositoryMock.findByUsername("Tester")).thenReturn(user);
        when(cartRepositoryMock.save(cart)).thenReturn(cart);

        final ResponseEntity<Cart> response = sut.removeFromcart(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(1, responseContent.getItems().size());
        assertEquals(2, responseContent.getTotal().intValue());
        assertEquals("Tester", responseContent.getUser().getUsername());
    }
}
