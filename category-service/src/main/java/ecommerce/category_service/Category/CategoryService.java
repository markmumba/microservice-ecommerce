package ecommerce.category_service.Category;

import ecommerce.proto_service.grpc.category.CategoryListResponse;
import ecommerce.proto_service.grpc.category.CategoryRequest;
import ecommerce.proto_service.grpc.category.CategoryResponse;
import ecommerce.proto_service.grpc.category.UpdateCategory;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory (CategoryRequest request);
    CategoryResponse getCategoryById(String id);
    List<CategoryListResponse> getAllCategories();
    String updateCategory(String id, UpdateCategory request);
    String deleteCategory(String id);
}
