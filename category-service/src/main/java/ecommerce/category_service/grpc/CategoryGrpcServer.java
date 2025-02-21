package ecommerce.category_service.grpc;


import com.google.protobuf.Empty;
import ecommerce.category_service.Category.CategoryService;
import ecommerce.proto_service.grpc.category.*;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;


@GrpcService
@AllArgsConstructor
public class CategoryGrpcServer extends CategoryServiceGrpc.CategoryServiceImplBase {

    private final CategoryService categoryService;

    @Override
    public void createCategory(CategoryRequest request, StreamObserver<CategoryResponse> responseObserver) {
        CategoryResponse response = categoryService.createCategory(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllCategories(Empty request, StreamObserver<CategoryList> responseObserver) {
        List<CategoryListResponse> categories = categoryService.getAllCategories();
        CategoryList response = CategoryList.newBuilder()
                .addAllCategories(categories)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getCategoryById(CategoryId request, StreamObserver<CategoryResponse> responseObserver) {

        CategoryResponse response =  categoryService.getCategoryById(request.getId());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateCategory(UpdateCategory request, StreamObserver<MessageResponse> responseObserver) {
        String message = categoryService.updateCategory(request.getId(),request);
        MessageResponse response = MessageResponse.newBuilder()
                .setResponse(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }


    @Override
    public void deleteCategory(CategoryId request, StreamObserver<MessageResponse> responseObserver) {
        String message = categoryService.deleteCategory(request.getId());
        MessageResponse response = MessageResponse.newBuilder()
                .setResponse(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
