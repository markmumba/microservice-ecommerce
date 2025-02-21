package ecommerce.order_service.order;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByUserId(String id);
    List<Order> findAllByOrderCode(String code);

}
