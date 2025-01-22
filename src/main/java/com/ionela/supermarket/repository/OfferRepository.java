package com.ionela.supermarket.repository;

import com.ionela.supermarket.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
