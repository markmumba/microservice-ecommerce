package ecommerce.order_service.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
public class Order {
    @Id
    private String id;
    private String userId;
    private String orderCode;
    private List<ProductOrder> products;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime updateTime;
}

