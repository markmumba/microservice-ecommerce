package ecommerce.inventory_service.gprc;

import com.google.protobuf.Empty;
import ecommerce.inventory_service.inventory.InventoryService;
import ecommerce.proto_service.grpc.inventory.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.NoSuchElementException;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public void addItemInventory(InventoryRequest request, StreamObserver<InventoryResponse> responseObserver) {
        try {
            InventoryResponse response = inventoryService.addItemInventory(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }

    }

    @Override
    public void getAllItemsInventory(Empty request, StreamObserver<InventoryList> responseObserver) {
        try {
            List<Inventory> inventories = inventoryService.getAllItemsInventory();
            InventoryList inventoryList = InventoryList.newBuilder()
                    .addAllItems(inventories)
                    .build();
            responseObserver.onNext(inventoryList);
            responseObserver.onCompleted();

        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }

    }

    @Override
    public void getItemInventoryById(InventoryId request, StreamObserver<Inventory> responseObserver) {
        try {
            Inventory inventory = inventoryService.getItemInventoryById(request);
            responseObserver.onNext(inventory);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void updateItemInventory(UpdateInventory request, StreamObserver<InventoryResponse> responseObserver) {
        try {
            InventoryResponse response = inventoryService.updateItemInventory(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void deleteItemInventory(InventoryId request, StreamObserver<InventoryResponse> responseObserver) {
        try {
            InventoryResponse response = inventoryService.deleteItemInventory(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }
}
