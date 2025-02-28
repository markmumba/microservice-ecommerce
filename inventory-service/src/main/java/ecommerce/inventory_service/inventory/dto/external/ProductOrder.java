package ecommerce.inventory_service.inventory.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrder{
    private String productId;
    private Integer quantity;

}

