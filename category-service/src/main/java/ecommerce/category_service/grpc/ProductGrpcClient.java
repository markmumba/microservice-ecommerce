package ecommerce.category_service.grpc;

import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceStub stub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newStub(channels.createChannel("product_service"));
    }
}
