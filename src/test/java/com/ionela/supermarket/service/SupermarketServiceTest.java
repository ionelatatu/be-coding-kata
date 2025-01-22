package com.ionela.supermarket.service;

import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.domain.Offer;
import com.ionela.supermarket.domain.OfferStatus;
import com.ionela.supermarket.model.ReceiptItemDTO;
import com.ionela.supermarket.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

class SupermarketServiceTest {

    @InjectMocks
    private SupermarketService supermarketService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testComputeCheckout_Success() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 3); // 3 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(2L, 2); // 2 Bananas

        Item apple = new Item(1L, "Apple", BigDecimal.valueOf(30.25), new ArrayList<>());
        Item banana = new Item(2L, "Banana", BigDecimal.valueOf(50), new ArrayList<>());

        List<Item> mockItems = Arrays.asList(apple, banana);
        when(itemRepository.findAllById(anySet())).thenReturn(mockItems);

        BigDecimal totalPrice = supermarketService.computeCheckout(Arrays.asList(item1, item2));
        assertEquals(BigDecimal.valueOf(190.75), totalPrice); // (3 * 30.25) + (2 * 50)
    }

    @Test
    void testComputeCheckout_SuccessWithInactiveOffer() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 3); // 3 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(2L, 2); // 2 Bananas

        Item apple = new Item(1L, "Apple", BigDecimal.valueOf(30.25), new ArrayList<>());
        Item banana = new Item(2L, "Banana", BigDecimal.valueOf(50), new ArrayList<>());

        Offer offer = new Offer(1L, 2, BigDecimal.valueOf(45), OfferStatus.INACTIVE, null); // 2 for 45
        apple.setOffers(new ArrayList<>(List.of(offer)));

        List<Item> mockItems = Arrays.asList(apple, banana);
        when(itemRepository.findAllById(anySet())).thenReturn(mockItems);

        BigDecimal totalPrice = supermarketService.computeCheckout(Arrays.asList(item1, item2));
        assertEquals(BigDecimal.valueOf(190.75), totalPrice); // (3 * 30.25) + (2 * 50)
    }

    @Test
    void testComputeCheckout_WithSingleOffer() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 1); // 1 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(2L, 1); // 1 Banana
        ReceiptItemDTO item3 = new ReceiptItemDTO(1L, 3); // 3 Apples

        Item apple = new Item(1L, "Apple", BigDecimal.valueOf(30), new ArrayList<>());
        Item banana = new Item(2L, "Banana", BigDecimal.valueOf(50), new ArrayList<>());

        Offer offer = new Offer(1L, 2, BigDecimal.valueOf(45), OfferStatus.ACTIVE, null); // 2 for 45
        apple.setOffers(new ArrayList<>(List.of(offer)));

        List<Item> mockItems = Arrays.asList(apple, banana);
        when(itemRepository.findAllById(anySet())).thenReturn(mockItems);

        BigDecimal totalPrice = supermarketService.computeCheckout(List.of(item1, item2, item3));
        assertEquals(BigDecimal.valueOf(140), totalPrice); // 2 * (2 for 45) + 50
    }

    @Test
    void testComputeCheckout_WithMultipleOffers() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 1); // 1 Apples
        ReceiptItemDTO item2 = new ReceiptItemDTO(2L, 3); // 3 Bananas
        ReceiptItemDTO item3 = new ReceiptItemDTO(1L, 4); // 4 Apples

        Item apple = new Item(1L, "Apple", BigDecimal.valueOf(35), new ArrayList<>());
        Item banana = new Item(2L, "Banana", BigDecimal.valueOf(50), new ArrayList<>());

        Offer offer1 = new Offer(1L, 3, BigDecimal.valueOf(60), OfferStatus.ACTIVE, null); // 3 for 60 for apples
        Offer offer2 = new Offer(2L, 2, BigDecimal.valueOf(45), OfferStatus.ACTIVE, null); // 2 for 45 for apples
        Offer offer3 = new Offer(3L, 2, BigDecimal.valueOf(80), OfferStatus.ACTIVE, null); // 2 for 80 for bananas
        Offer offer4 = new Offer(3L, 2, BigDecimal.valueOf(75), OfferStatus.INACTIVE, null);

        apple.setOffers(new ArrayList<>(List.of(offer1, offer2)));
        banana.setOffers(new ArrayList<>(List.of(offer3, offer4)));

        List<Item> mockItems = Arrays.asList(apple, banana);
        when(itemRepository.findAllById(anySet())).thenReturn(mockItems);

        BigDecimal totalPrice = supermarketService.computeCheckout(List.of(item1, item2, item3));
        assertEquals(BigDecimal.valueOf(235), totalPrice); // (3 for 60) + (2 for 45) + (2 for 80) + (1 for 50)
    }

    @Test
    void testComputeCheckout_ItemNotFound() {
        ReceiptItemDTO item1 = new ReceiptItemDTO(1L, 3);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> supermarketService.computeCheckout(List.of(item1)));
    }
}
