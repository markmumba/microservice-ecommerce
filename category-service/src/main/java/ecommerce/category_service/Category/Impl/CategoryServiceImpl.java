package ecommerce.category_service.Category.Impl;

import ecommerce.category_service.Category.Category;
import ecommerce.category_service.Category.CategoryRepository;
import ecommerce.category_service.Category.CategoryService;
import ecommerce.category_service.Category.dto.CategoryMapper;
import ecommerce.proto_service.grpc.category.CategoryListResponse;
import ecommerce.proto_service.grpc.category.CategoryRequest;
import ecommerce.proto_service.grpc.category.CategoryResponse;
import ecommerce.proto_service.grpc.category.UpdateCategory;
import ecommerce.proto_service.grpc.product.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author markianmwangi
 *
 */

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductServiceGrpc.ProductServiceStub productStub;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.fromEntity(savedCategory, Collections.emptyList());
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Category with given id not available")
        );
        ProductCategoryId categoryId = ProductCategoryId.newBuilder().setId(category.getId()).build();
        List<ProductItem> productItems =  new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        productStub.getProductsByCategory(categoryId, new StreamObserver<ProductListResponse>() {
            @Override
            public void onNext(ProductListResponse value) {
                productItems.addAll(value.getProductsList());

            }
            @Override
            public void onError(Throwable t) {
                System.err.println("Error receiving gRPC stream: " + t.getMessage());
                latch.countDown();
            }
            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        try {
            latch.await();
        }catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for grpc response ",e);
        }
        return categoryMapper.fromEntity(category,productItems);
    }

    @Override
    public List<CategoryListResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::fromEntityList)
                .toList();
    }

    @Override
    public String updateCategory(String id, UpdateCategory request) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Category with given id not available")
        );

        if ( !request.getName().isEmpty()) {
            category.setName(request.getName());
        }
        if (  !request.getDescription().isEmpty()) {
            category.setDescription(request.getDescription());
        }

        categoryRepository.save(category);
        return "Category updated successfully";
    }


    @Override
    public String deleteCategory(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Category with given id not available")
        );
        categoryRepository.delete(category);
        return "Category deleted successfully";

    }
}
