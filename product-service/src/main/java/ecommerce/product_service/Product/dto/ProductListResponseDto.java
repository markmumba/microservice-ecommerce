package ecommerce.product_service.Product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListResponseDto {
    private String id ;
    private String name;
    private BigDecimal price;
}
