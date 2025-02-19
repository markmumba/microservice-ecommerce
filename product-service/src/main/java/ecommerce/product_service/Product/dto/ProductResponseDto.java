package ecommerce.product_service.Product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private String id ;
    private String productCode;
    private String name;
    private BigDecimal price;
    private String categoryId;
}
