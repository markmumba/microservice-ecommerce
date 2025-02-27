package ecommerce.inventory_service.gprc;

import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productStub (GrpcChannelFactory channel) {
        return ProductServiceGrpc.newBlockingStub(channel.createChannel("product_service"));
    }

}
