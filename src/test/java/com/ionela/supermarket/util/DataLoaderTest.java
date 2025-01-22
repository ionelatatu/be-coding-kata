package com.ionela.supermarket.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    private DataLoader dataLoader;
    private ItemRepository itemRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        dataLoader = new DataLoader();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoadDataFromFile() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/test-data.json");
        List<Item> items = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));

        for (Item item : items) {
            itemRepository.save(item);
        }
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(4)).save(itemCaptor.capture());

        List<Item> savedItems = itemCaptor.getAllValues();
        assertEquals(4, savedItems.size());
        assertEquals("Apple", savedItems.get(0).getName());
        assertEquals("Banana", savedItems.get(1).getName());
        assertEquals(50.00, savedItems.get(1).getPrice().doubleValue());
        assertEquals(1, savedItems.get(0).getOffers().size());
    }

    @Test
    void testLoadDataFileNotFound() {
        DataLoader dataLoaderSpy = Mockito.spy(dataLoader);
        doReturn(null).when(dataLoaderSpy).getClass().getResourceAsStream("/test-data.json");

        assertThrows(
                NullPointerException.class,
                () -> dataLoaderSpy.loadData(itemRepository).run(),
                "Expected to throw an NullPointerException for missing file"
        );

        verify(itemRepository, never()).save(any());
        assertEquals(0, itemRepository.findAll().size());
    }

}
