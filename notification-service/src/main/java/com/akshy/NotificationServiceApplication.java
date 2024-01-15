package com.akshy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic", groupId = "notificationId")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got message <{}>", orderPlacedEvent);
        // send out an email notification
    }

}