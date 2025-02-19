package ecommerce.category_service.Category.dto;

import ecommerce.category_service.Category.Category;
import ecommerce.proto_service.grpc.category.CategoryListResponse;
import ecommerce.proto_service.grpc.category.CategoryProductItem;
import ecommerce.proto_service.grpc.category.CategoryRequest;
import ecommerce.proto_service.grpc.category.CategoryResponse;
import ecommerce.proto_service.grpc.product.ProductItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryMapper {

    public Category toEntity (CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }


    public CategoryResponse fromEntity (Category category, List<ProductItem> products) {
        List<CategoryProductItem> productItems = products.stream()
                .map(product -> CategoryProductItem.newBuilder()
                        .setId(product.getId())
                        .setName(product.getName())
                        .setPrice(product.getPrice())
                        .build())
                .toList();
        return CategoryResponse.newBuilder()
                 .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription())
                .addAllProducts(productItems) // Set the product list
                .build();
    }

    public CategoryListResponse fromEntityList (Category category){
        return CategoryListResponse.newBuilder().
    }

}
