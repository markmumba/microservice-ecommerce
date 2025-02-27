package ecommerce.order_service.order.impl;

import ecommerce.order_service.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCreatedOrder(String order) {
        String orderCreatedTopic = "order-created";
        kafkaTemplate.send(orderCreatedTopic,order);
    }
}
