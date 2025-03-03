package ecommerce.order_service.order;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByUserId(String id);
    Optional<Order> findByOrderCode(String code);

    List<Order> findByOrderDateBetween(LocalDateTime startDate,LocalDateTime endDate);

}
