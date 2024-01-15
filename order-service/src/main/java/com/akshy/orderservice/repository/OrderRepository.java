package com.akshy.orderservice.repository;

import com.akshy.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

//Doest need @Repository annotation as interface extends JpaRepository spring can automatically detect it.
public interface OrderRepository extends JpaRepository<Order, Long> {
}
/*
when an interface extends JpaRepository, Spring Data JPA provides an
implementation for that interface. This implementation is annotated
with @Repository. So, if you are injecting the OrderRepository interface
into your service, it's effectively injecting the Spring Data JPA-generated
implementation, which is already annotated with @Repository.
*/
