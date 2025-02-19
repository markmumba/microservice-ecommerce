package ecommerce.product_service.Product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("products")
@Data
public class Product {
    @Id
    private String id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private String categoryId;
}


