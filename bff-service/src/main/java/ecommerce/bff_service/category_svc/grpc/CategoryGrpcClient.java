package ecommerce.bff_service.category_svc.grpc;

import ecommerce.proto_service.grpc.category.CategoryServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

@Service
public class CategoryGrpcClient {

    @Bean
    CategoryServiceGrpc.CategoryServiceStub asyncCategoryStub(GrpcChannelFactory channel) {
        return CategoryServiceGrpc.newStub(channel.createChannel("category_service"));
    }

    @Bean
    CategoryServiceGrpc.CategoryServiceBlockingStub categoryStub(GrpcChannelFactory channel) {
        return CategoryServiceGrpc.newBlockingStub(channel.createChannel("category_service"));
    }

}

