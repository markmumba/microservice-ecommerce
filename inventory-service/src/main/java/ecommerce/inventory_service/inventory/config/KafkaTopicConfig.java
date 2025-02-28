package ecommerce.inventory_service.inventory.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig{

    @Bean
    public NewTopic orderResponse() {
        return TopicBuilder.name("order-response").build();
    }
}
