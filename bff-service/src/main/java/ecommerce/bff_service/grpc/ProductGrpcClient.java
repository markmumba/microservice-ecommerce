package ecommerce.bff_service.grpc;

import ecommerce.proto_service.grpc.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;


@Service
public class ProductGrpcClient {
    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub stub (GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("0.0.0.0:9090"));
    }

}
