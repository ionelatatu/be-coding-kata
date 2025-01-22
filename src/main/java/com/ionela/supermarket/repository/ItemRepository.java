package com.ionela.supermarket.repository;

import com.ionela.supermarket.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
