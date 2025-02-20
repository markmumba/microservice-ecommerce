package ecommerce.bff_service.product_svc.grpc;

import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;


@Service
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceStub asyncProductStub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newStub(channels.createChannel("product_service"));
    }

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productStub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("product_service"));
    }


}
