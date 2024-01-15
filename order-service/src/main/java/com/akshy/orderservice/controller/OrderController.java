package com.akshy.orderservice.controller;

import com.akshy.orderservice.dto.OrderRequest;
import com.akshy.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="inventory",fallbackMethod = "fallbackmethod")
    @TimeLimiter(name = "inventory")
    @Retry(name="inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest){

        return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequest));
    }
    public CompletableFuture<String>  fallbackmethod(OrderRequest orderRequest,RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()->"Oops! something went wrong, please try again later");

    }
}
