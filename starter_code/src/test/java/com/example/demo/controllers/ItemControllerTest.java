package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ItemControllerTest {

    private ItemController sut;

    private ItemRepository itemRepositoryMock = mock(ItemRepository.class);

    @Before
    public void setup() {
        sut = new ItemController();
        TestUtils.injectDependency(sut, "itemRepository", itemRepositoryMock);
    }

    @Test
    public void getItemsByName_forValidUsername_isSuccessful() {
        String input = "Tester";
        
        when(itemRepositoryMock.findByName(input)).thenReturn(List.of(new Item(), new Item()));
        
        final ResponseEntity<List<Item>> response = sut.getItemsByName(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> responseContent = response.getBody();
        assertNotNull(responseContent);
        assertEquals(2, responseContent.size());
 
    }

    @Test
    public void getItemsByName_forInvalidUser_returnsNotFound() {
        String input = "Tester";
        
        when(itemRepositoryMock.findByName(input)).thenReturn(null);
        
        final ResponseEntity<List<Item>> response = sut.getItemsByName(input);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<Item> responseContent = response.getBody();
        assertNull(responseContent);
 
    }
}
