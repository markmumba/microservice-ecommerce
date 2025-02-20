package ecommerce.bff_service.product_svc.grpc;

import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;


@Service
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceStub asyncStub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newStub(channels.createChannel("product-service"));
    }

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub stub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("product-service"));
    }


}
