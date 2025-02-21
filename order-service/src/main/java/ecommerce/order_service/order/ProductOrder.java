package ecommerce.order_service.order;

import lombok.Data;

@Data
public class ProductOrder{
    private String productId;
    private Integer quantity;
}
