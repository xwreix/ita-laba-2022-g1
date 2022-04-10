package com.itechart.food_delivery.repository;

import com.itechart.food_delivery.model.OrderAndFoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderAndFoodOrderRepository extends JpaRepository<OrderAndFoodOrder, Long> {
    Optional<OrderAndFoodOrder> findByOrderId(Long orderId);
}
