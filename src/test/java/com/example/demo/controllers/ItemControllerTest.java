package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setPrice(BigDecimal.valueOf(9.99));
        item1.setDescription("Test Description 1");

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setPrice(BigDecimal.valueOf(19.99));
        item2.setDescription("Test Description 2");
    }

    @Test
    public void testGetItems_Success() {
        List<Item> items = Arrays.asList(item1, item2);
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testGetItemById_ItemFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item1, response.getBody());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemById_ItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemsByName_Success() {
        List<Item> items = Arrays.asList(item1);
        when(itemRepository.findByName("Test Item 1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Test Item 1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(itemRepository, times(1)).findByName("Test Item 1");
    }

    @Test
    public void testGetItemsByName_NotFound() {
        when(itemRepository.findByName("Non-existent Item")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Non-existent Item");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemRepository, times(1)).findByName("Non-existent Item");
    }
}