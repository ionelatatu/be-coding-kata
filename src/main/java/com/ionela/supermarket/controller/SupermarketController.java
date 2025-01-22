package com.ionela.supermarket.controller;

import com.ionela.supermarket.model.ReceiptItemDTO;
import com.ionela.supermarket.service.SupermarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/supermarket")
public class SupermarketController {
    @Autowired
    private SupermarketService supermarketService;

    @PostMapping("/checkout")
    public BigDecimal computeCheckout(@RequestBody List<ReceiptItemDTO> receiptItems) {
        return supermarketService.computeCheckout(receiptItems);
    }
}
