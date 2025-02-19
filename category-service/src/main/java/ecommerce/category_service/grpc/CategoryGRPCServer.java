package ecommerce.category_service.grpc;


import com.google.protobuf.Empty;
import ecommerce.category_service.Category.CategoryService;
import ecommerce.proto_service.grpc.category.*;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
@AllArgsConstructor
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public class CategoryGRPCServer extends CategoryServiceGrpc.CategoryServiceImplBase {

    private final CategoryService categoryService;

    @Override
    public void createCategory(CategoryRequest request, StreamObserver<CategoryResponse> responseObserver) {
        super.createCategory(request, responseObserver);
    }

    @Override
    public void getAllCategories(Empty request, StreamObserver<CategoryList> responseObserver) {
        super.getAllCategories(request, responseObserver);
    }

    @Override
    public void getCategoryById(CategoryId request, StreamObserver<CategoryResponse> responseObserver) {
        super.getCategoryById(request, responseObserver);
    }

    @Override
    public void updateCategory(UpdateCategory request, StreamObserver<MessageResponse> responseObserver) {
        super.updateCategory(request, responseObserver);
    }

    @Override
    public void deleteCategory(CategoryId request, StreamObserver<MessageResponse> responseObserver) {
        super.deleteCategory(request, responseObserver);
    }
}
