package com.ionela.supermarket.service;

import com.ionela.supermarket.domain.Item;
import com.ionela.supermarket.model.ItemDTO;
import com.ionela.supermarket.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Updates the price of an item and returns the updated item.
     *
     * @param itemId the ID of the item to update
     * @param newPrice the new price to set for the item
     * @return the updated item
     * @throws IllegalArgumentException if the item is not found
     */
    public ItemDTO updatePrice(Long itemId, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        item.setPrice(newPrice);
        Item newItem = itemRepository.save(item);

        return toItemDTO(newItem);
    }

    public ItemDTO toItemDTO(Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getPrice());
    }
}
