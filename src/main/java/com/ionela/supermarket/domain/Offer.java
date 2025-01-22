package com.ionela.supermarket.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private BigDecimal price;

    private OfferStatus status;

    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false)
    private Item item;

    @Override
    public String toString() {
        return "Offer{" +
                "quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
