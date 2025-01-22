package com.ionela.supermarket.service;

import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.domain.Offer;
import com.ionela.supermarket.domain.OfferStatus;
import com.ionela.supermarket.model.ReceiptItemDTO;
import com.ionela.supermarket.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupermarketService {
    private static final Logger logger = LoggerFactory.getLogger(SupermarketService.class);

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Computes the total checkout price for a list of receipt items.
     * It assumes that for an item there can be multiple offers for different quantities
     *
     * @param receiptItems a list of ReceiptItemDTO objects, where each object contains the item ID and the quantity of that item
     * @return the total price as a BigDecimal
     * @throws IllegalArgumentException if any item in the receipt is not found in the database
     */
    public BigDecimal computeCheckout(List<ReceiptItemDTO> receiptItems) {
        logger.info("Starting checkout computation for {} items", receiptItems.size());
        Map<Long, Integer> itemQuantities = createQuantitiesMap(receiptItems);

        BigDecimal totalPrice = calculateTotalPrice(itemQuantities);
        logger.info("Total price calculated is: {}", totalPrice);

        return totalPrice;
    }

    private Map<Long,Integer> createQuantitiesMap(List<ReceiptItemDTO> receiptItems) {
        Map<Long, Integer> itemQuantities = new HashMap<>();
        for (ReceiptItemDTO dto : receiptItems) {
            itemQuantities.put(dto.itemId(), itemQuantities.getOrDefault(dto.itemId(), 0) + dto.quantity());
        }
        return itemQuantities;
    }

    private BigDecimal calculateTotalPrice(Map<Long, Integer> itemQuantities) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Find all items required for the checkout
        Map<Long, Item> items = itemRepository.findAllById(itemQuantities.keySet())
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
        logger.debug("Fetched items: {}", items.keySet());

        // Iterate through each item and calculate its total price
        for(Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            Item item = items.get(itemId);
            if (item == null) {
                logger.error("Item not found for id: {}", itemId);
                throw new IllegalArgumentException("Item not found: " + itemId);
            }

            logger.debug("Processing item: {}, quantity: {}", item.getName(), quantity);

            // If quantity > 1, compute the price with offers; otherwise, use the regular price
            if(quantity > 1) {
                totalPrice = totalPrice.add(computePricePerItem(item, quantity));
            } else {
                logger.debug("Using fixed price:{} for item: {}", item.getPrice(), item.getName());
                totalPrice = totalPrice.add(item.getPrice());
            }
        }
        return totalPrice;
    }

    private BigDecimal computePricePerItem(Item item, Integer quantity) {
        // Retrieve the offers associated with the item
        List<Offer> offers = item.getOffers().stream()
                .filter(o -> o.getStatus() == OfferStatus.ACTIVE)
                .collect(Collectors.toList());

        if (offers.isEmpty()) {
            logger.debug("No offers available for item: {}. Using regular price {}.", item.getName(), item.getPrice());
            return BigDecimal.valueOf(quantity).multiply(item.getPrice());
        }

        // Sort offers by quantity in descending order (highest quantity first)
        offers.sort((o1, o2) -> Integer.compare(o2.getQuantity(), o1.getQuantity()));
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Offer offer : offers) {
            if (quantity == 0) break;

            // Number of times this offer can be applied
            int applicableQuantity = quantity / offer.getQuantity();

            quantity %= offer.getQuantity();

            logger.debug("Applying offer: {} for item: {}, applicable quantity: {}",
                    offer, item.getName(), applicableQuantity);
            totalPrice = totalPrice.add(BigDecimal.valueOf(applicableQuantity).multiply(offer.getPrice()));
        }

        // Calculate the price for any remaining items at the regular price
        if (quantity > 0) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(quantity).multiply(item.getPrice()));
        }
        return totalPrice;
    }
}
