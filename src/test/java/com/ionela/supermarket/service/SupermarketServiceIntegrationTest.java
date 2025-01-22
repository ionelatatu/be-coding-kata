package com.ionela.supermarket.service;

import com.ionela.supermarket.model.ReceiptItemDTO;
import com.ionela.supermarket.repository.ItemRepository;
import com.ionela.supermarket.repository.OfferRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SupermarketService.
 * Note: Test data is loaded from the test-data.json file via the DataLoader.
 *   that the data in the file is consistent with the expected test cases.
 */
@SpringBootTest
@Transactional
class SupermarketServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private SupermarketService supermarketService;

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        offerRepository.deleteAll();
    }

    @Test
    void testComputePrice_SimpleComputation() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 1); // 1 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(3L, 1); // 1 Peach
        List<ReceiptItemDTO> receiptItems = List.of(item1, item2);

        BigDecimal totalPrice = supermarketService.computeCheckout(receiptItems);

        assertNotNull(totalPrice);
        assertEquals(0, totalPrice.compareTo(BigDecimal.valueOf(90.00)));
    }

    @Test
    void testComputeCheckout_WithMultipleOffers() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 1); // 1 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(2L, 3); // 3 Bananas
        ReceiptItemDTO item3 = new ReceiptItemDTO(1L, 3); // 3 Apples
        ReceiptItemDTO item4 = new ReceiptItemDTO(3L, 2); // 2 Peaches

        List<ReceiptItemDTO> receiptItems = List.of(item1, item2, item3, item4);

        BigDecimal totalPrice = supermarketService.computeCheckout(receiptItems);
        assertEquals(0, totalPrice.compareTo(BigDecimal.valueOf(340.00)));// (3 for 60) + (1 for 30) + (2 for 80) + (1 for 50) + (2 for 60)
    }

    @Test
    void testComputePrice_ItemNotFound() {
        ReceiptItemDTO invalidItemDTO = new ReceiptItemDTO(999L, 2); // Invalid item id
        List<ReceiptItemDTO> receiptItems = List.of(invalidItemDTO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> supermarketService.computeCheckout(receiptItems)
        );

        assertEquals("Item not found: 999", exception.getMessage());
    }
}
