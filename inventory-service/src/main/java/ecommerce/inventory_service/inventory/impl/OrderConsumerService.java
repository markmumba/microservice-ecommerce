package ecommerce.inventory_service.inventory.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderConsumerService {

    @KafkaListener(topics = "order-created",groupId = "orders")
    public String getOrder(String order) {
        log.info("*".repeat(30));
        log.info("This is what we receive from kafka");
        log.info(order);
        log.info("");
        return order;
    }
}
