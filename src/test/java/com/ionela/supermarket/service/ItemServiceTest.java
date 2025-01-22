package com.ionela.supermarket.service;

import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.model.ItemDTO;
import com.ionela.supermarket.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePrice_Success() {
        Long itemId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(20.00);
        Item item = new Item(itemId, "Apple", BigDecimal.valueOf(30.00), null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDTO result = itemService.updatePrice(itemId, newPrice);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(newPrice, result.getPrice());
    }

    @Test
    void testUpdatePrice_ItemNotFound() {
        Long itemId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(20.00);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.updatePrice(itemId, newPrice));

        assertEquals("Item not found: " + itemId, exception.getMessage());
    }

    @Test
    void testUpdatePrice_InvalidPrice() {
        Long itemId = 1L;
        BigDecimal invalidPrice = BigDecimal.ZERO;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.updatePrice(itemId, invalidPrice));

        assertEquals("Price must be greater than zero", exception.getMessage());
    }
}
