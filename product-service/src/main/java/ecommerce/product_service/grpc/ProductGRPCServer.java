package ecommerce.product_service.grpc;

import ecommerce.product_service.Product.ProductService;
import ecommerce.proto_service.grpc.product.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

/**
 * @author markianmwangi
 *
 */

@GrpcService
@AllArgsConstructor
public class ProductGRPCServer extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    @Override
    public void createProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        ProductResponse response = productService.createProduct(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProducts(Empty request, StreamObserver<ProductListResponse> responseObserver) {
        List<ProductItem> productItems = productService.getAllProducts();

        ProductListResponse response = ProductListResponse.newBuilder()
                .addAllProducts(productItems)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsByCategory(ProductCategoryId request, StreamObserver<ProductListResponse> responseObserver) {

        List<ProductItem> productItems = productService.getProductsByCategory(request.getId());

        ProductListResponse response = ProductListResponse.newBuilder()
                .addAllProducts(productItems)
                        .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getProductById(ProductId request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponse response = productService.getProductById(request.getId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Error finding product: " + e.getMessage())
                            .asException()
            );
        }
    }

    @Override
    public void updateProduct(UpdateRequest request, StreamObserver<MessageResponse> responseObserver) {
        String response =  productService.updateProduct(request.getId(),request);
       MessageResponse messageResponse = MessageResponse.newBuilder()
               .setResponse(response)
               .build();
        responseObserver.onNext(messageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(ProductId request, StreamObserver<MessageResponse> responseObserver) {
        String response = productService.deleteProduct(request.getId());
         MessageResponse messageResponse =MessageResponse.newBuilder()
                        .setResponse(response)
                                .build();
        responseObserver.onNext(messageResponse);
        responseObserver.onCompleted();
    }
}
