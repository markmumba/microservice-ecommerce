package ecommerce.bff_service.inventory_svc.grpc;


import ecommerce.proto_service.grpc.inventory.InventoryServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class InventoryGrpcClient {

    @Bean
    InventoryServiceGrpc.InventoryServiceStub asyncInventoryClient (GrpcChannelFactory channel) {
        return  InventoryServiceGrpc.newStub(channel.createChannel("inventory_service"));
    }

    @Bean
    InventoryServiceGrpc.InventoryServiceBlockingStub inventoryClient (GrpcChannelFactory channel){
        return InventoryServiceGrpc.newBlockingStub(channel.createChannel("inventory_service"));
    }

}
