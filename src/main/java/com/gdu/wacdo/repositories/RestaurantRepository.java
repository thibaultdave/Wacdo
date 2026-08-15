package com.gdu.wacdo.repositories;

import com.gdu.wacdo.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}