package ecommerce.inventory_service.inventory.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String userId;
    private String orderCode;
    private List<ProductOrder> products;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime updateTime;
}


