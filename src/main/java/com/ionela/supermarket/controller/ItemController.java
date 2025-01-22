package com.ionela.supermarket.controller;

import com.ionela.supermarket.model.ItemDTO;
import com.ionela.supermarket.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItemPrice(@PathVariable Long id, @RequestBody BigDecimal newPrice) {
        ItemDTO item = itemService.updatePrice(id, newPrice);
        return ResponseEntity.ok(item);
    }
}
