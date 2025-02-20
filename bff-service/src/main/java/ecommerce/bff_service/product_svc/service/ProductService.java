package ecommerce.bff_service.product_svc.service;

import com.example.demo.codegen.types.Product;
import com.example.demo.codegen.types.ProductInput;
import ecommerce.bff_service.product_svc.mapper.ProductMapper;
import ecommerce.proto_service.grpc.product.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author markianmwangi
 * Service class that will be using the mapper to transform java objects to grpc and back
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService{

    private final ProductServiceGrpc.ProductServiceStub productClient;
    private final ProductServiceGrpc.ProductServiceBlockingStub productBlockingClient;

    /**
     *
     * method that will get the stream of products and give an array of product objects
     * @return
     * the array of product objects
     */

    public CompletableFuture<List<Product>> getAllProducts () {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();

        List<Product> products = new ArrayList<>();
        Empty request = Empty.newBuilder().build();

        productClient.getProducts(request, new StreamObserver<>() {
            @Override
            public void onNext(ProductListResponse value) {
                products.addAll(ProductMapper.INSTANCE.grpcToDtos(value.getProductsList()));
            }
            @Override
            public void onError(Throwable t) {
                log.error("Error receiving products: {}",t.getMessage());
                future.completeExceptionally(t);
            }
            @Override
            public void onCompleted() {
                log.info("All products received successfully");
                future.complete(products);
            }
        });
        return future;
    }

    public Product createProduct (ProductInput input) {
        ProductRequest request =  ProductMapper.INSTANCE.dtoToGrpc(input);
        ProductResponse response = productBlockingClient.createProduct(request);
        return ProductMapper.INSTANCE.grpcToDto(response);
    }

    public Product getProductById (String id) {
        ProductId productId = ProductId.newBuilder().setId(id).build();
        ProductResponse response = productBlockingClient.getProductById(productId);
        return ProductMapper.INSTANCE.grpcToDto(response);
    }

    public String updateProduct (ProductInput input ) {
        UpdateRequest request = ProductMapper.INSTANCE.updateDtoToGrpcUpdate(input);
        MessageResponse message = productBlockingClient.updateProduct(request);
        return ProductMapper.INSTANCE.messageGrpcToDtoMessage(message);
    }

    public String deleteProduct (String id) {
        ProductId productId = ProductId.newBuilder().setId(id).build();
        MessageResponse message = productBlockingClient.deleteProduct(productId);
        return ProductMapper.INSTANCE.messageGrpcToDtoMessage(message);
    }



}
