package com.ionela.supermarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private BigDecimal price;
}
