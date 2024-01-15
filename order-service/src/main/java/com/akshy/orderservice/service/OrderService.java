package com.akshy.orderservice.service;

import com.akshy.orderservice.event.OrderPlacedEvent;
import com.akshy.orderservice.dto.InventoryResponse;
import com.akshy.orderservice.dto.OrderLineItemsDto;
import com.akshy.orderservice.dto.OrderRequest;
import com.akshy.orderservice.model.Order;
import com.akshy.orderservice.model.OrderLineItems;
import com.akshy.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                .collect(Collectors.toList());//use Collectors.toList() to create mutable collectors list as .tolist() creates immutable list which causes error as hibernate may try to change the list


        order.setOrderLineItemsList(orderLineItems);

        List<String>skuCodes = order.getOrderLineItemsList().stream()
                .map(orderLineItem -> orderLineItem.getSkuCode())
                .toList();

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve().bodyToMono(InventoryResponse[].class).block();

        boolean allProductsInStock =  Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.isInStock());
        if(allProductsInStock){
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed Sucessfully!";
        } else{
            throw new IllegalArgumentException("Product is not in stock, please try again later!");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
