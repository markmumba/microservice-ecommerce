package ecommerce.order_service.grpc;

import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productStub (GrpcChannelFactory channel) {
        return ProductServiceGrpc.newBlockingStub(channel.createChannel("product_service"));
    }
}
