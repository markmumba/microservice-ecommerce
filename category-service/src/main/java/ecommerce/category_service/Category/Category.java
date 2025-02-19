package ecommerce.category_service.Category;

import ecommerce.category_service.Category.dto.external.Product;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("categories")
@Data
public class Category {
    @Id
    private String id;
    private String name;
    private String description;
    private List<Product> products;
}
