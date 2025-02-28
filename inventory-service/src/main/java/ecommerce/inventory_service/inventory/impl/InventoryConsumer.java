package ecommerce.inventory_service.inventory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.inventory_service.inventory.InventoryService;
import ecommerce.inventory_service.inventory.dto.external.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-created",groupId = "orders")
    public void getOrder(String order) throws JsonProcessingException {
        Order order1 = objectMapper.readValue(order, Order.class);
        inventoryService.updateItemsInventoryFromOrder(order1);
    }
}
