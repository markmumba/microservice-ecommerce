package ecommerce.inventory_service.gprc;

import com.google.protobuf.Empty;
import ecommerce.proto_service.grpc.inventory.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {


    @Override
    public void addItemInventory(InventoryRequest request, StreamObserver<InventoryResponse> responseObserver) {
        super.addItemInventory(request, responseObserver);
    }

    @Override
    public void getAllItemsInventory(Empty request, StreamObserver<InventoryList> responseObserver) {
        super.getAllItemsInventory(request, responseObserver);
    }

    @Override
    public void getItemInventoryById(InventoryId request, StreamObserver<Inventory> responseObserver) {
        super.getItemInventoryById(request, responseObserver);
    }

    @Override
    public void updateItemInventory(UpdateInventory request, StreamObserver<InventoryResponse> responseObserver) {
        super.updateItemInventory(request, responseObserver);
    }

    @Override
    public void deleteItemInventory(InventoryId request, StreamObserver<InventoryResponse> responseObserver) {
        super.deleteItemInventory(request, responseObserver);
    }
}
