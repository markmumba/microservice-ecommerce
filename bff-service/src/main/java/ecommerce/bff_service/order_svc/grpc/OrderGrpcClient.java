package ecommerce.bff_service.order_svc.grpc;


import ecommerce.proto_service.grpc.order.OrderServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderGrpcClient {
    @Bean
    OrderServiceGrpc.OrderServiceBlockingStub orderClient(GrpcChannelFactory channel) {
        return OrderServiceGrpc.newBlockingStub(channel.createChannel("order_service"));
    }

    @Bean
    OrderServiceGrpc.OrderServiceStub asyncOrderClient(GrpcChannelFactory channel) {
        return OrderServiceGrpc.newStub(channel.createChannel("order_service"));
    }
}
