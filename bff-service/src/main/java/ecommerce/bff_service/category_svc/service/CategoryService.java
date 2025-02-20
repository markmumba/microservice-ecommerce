package ecommerce.bff_service.category_svc.service;


import com.example.demo.codegen.types.Category;
import com.example.demo.codegen.types.CategoryInput;
import com.example.demo.codegen.types.CategoryList;
import com.google.protobuf.Empty;
import ecommerce.bff_service.category_svc.mapper.CategoryMapper;
import ecommerce.proto_service.grpc.category.*;
import ecommerce.proto_service.grpc.product.UpdateRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryServiceGrpc.CategoryServiceBlockingStub categoryClient;
    private final CategoryServiceGrpc.CategoryServiceStub asyncCategoryClient;

    public CompletableFuture<List<CategoryList>> getAllCategories() {
        CompletableFuture<List<CategoryList>> future = new CompletableFuture<>();
        List<CategoryList> categories = new ArrayList<>();
        Empty empty = Empty.newBuilder().build();

        asyncCategoryClient.getAllCategories(empty, new StreamObserver<>() {
            @Override
            public void onNext(ecommerce.proto_service.grpc.category.CategoryList value) {
                categories.addAll(CategoryMapper.INSTANCE.grpcToDtos(value.getCategoriesList()));
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error getting all categories {}", t.getMessage());
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                future.complete(categories);
            }
        });

        return future;
    }
    public Category getCategoryById(String id) {
        CategoryId categoryId = CategoryId.newBuilder().setId(id).build();
        CategoryResponse response = categoryClient.getCategoryById(categoryId);
        return CategoryMapper.INSTANCE.grpcCategoryToDtoCategory(response);
    }
    public Category createCategory(CategoryInput input) {
        CategoryRequest request = CategoryMapper.INSTANCE.dtoCategoryReqToGrpcReq(input);
        CategoryResponse response = categoryClient.createCategory(request);
        return CategoryMapper.INSTANCE.grpcCategoryToDtoCategory(response);
    }
    public String updateCategory(CategoryInput input) {
        UpdateCategory request = CategoryMapper.INSTANCE.dtUpdateReqToGrpcReq(input);
        MessageResponse response = categoryClient.updateCategory(request);
        return response.getResponse();
    }
    public String deleteCategory(String id) {
        CategoryId categoryId = CategoryId.newBuilder().setId(id).build();
        MessageResponse response = categoryClient.deleteCategory(categoryId);
        return response.getResponse();
    }
}
