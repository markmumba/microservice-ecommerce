package ecommerce.inventory_service.inventory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InventoryProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void inventoryResponseOrder(String response) {
        kafkaTemplate.send("order-response",response);
    }
}
