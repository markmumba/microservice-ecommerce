package ecommerce.category_service.Category.dto;

import ecommerce.category_service.Category.dto.external.Product;
import lombok.Data;

import java.util.List;

@Data
public class CategoryResponseDto {
    private String id;
    private String name;
    private String description;
    private List<Product> products;
}
