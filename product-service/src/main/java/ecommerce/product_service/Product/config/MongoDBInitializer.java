package ecommerce.product_service.Product.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@RequiredArgsConstructor
public class MongoDBInitializer {

    private final MongoTemplate mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void refreshDatabase () {
        System.out.println("Dropping all MongoDB collections...");
        mongoTemplate.getDb().drop();
        System.out.println("Database dropped and will be recreated on first insert.");

    }

}
