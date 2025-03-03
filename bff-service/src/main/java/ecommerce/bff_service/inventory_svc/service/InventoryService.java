package ecommerce.bff_service.inventory_svc.service;

import com.example.demo.codegen.types.Inventory;
import com.example.demo.codegen.types.InventoryInput;
import com.example.demo.codegen.types.InventoryResponse;
import com.example.demo.codegen.types.UpdateInventoryInput;
import com.google.protobuf.Empty;
import ecommerce.bff_service.inventory_svc.mapper.InventoryMapper;
import ecommerce.proto_service.grpc.inventory.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryClient;

    private final InventoryServiceGrpc.InventoryServiceStub asyncInventoryClient;


    public CompletableFuture<List<Inventory>> getAllItemInventory() {
        CompletableFuture<List<Inventory>> future = new CompletableFuture<>();

        List<ecommerce.proto_service.grpc.inventory.Inventory> inventories = new ArrayList<>();

        asyncInventoryClient.getAllItemsInventory(Empty.newBuilder().build(), new StreamObserver<InventoryList>() {
            @Override
            public void onNext(InventoryList value) {
                inventories.addAll(value.getItemsList());
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error getting all inventories {}", t.getMessage());

            }

            @Override
            public void onCompleted() {
                List<Inventory> dgsInventory = inventories.stream().map(InventoryMapper.INSTANCE::mapToDgs).toList();
                future.complete(dgsInventory);
            }
        });
        return future;
    }

    public Inventory getItemInventoryById(String id) {
        InventoryId inventoryId = InventoryId.newBuilder().setId(id).build();
        return InventoryMapper.INSTANCE.mapToDgs(inventoryClient.getItemInventoryById(inventoryId));
    }


    public InventoryResponse addItemInventory(InventoryInput input) {
        InventoryRequest inventoryRequest = InventoryMapper.INSTANCE.mapToGrpcRequest(input);
        ecommerce.proto_service.grpc.inventory.InventoryResponse response = inventoryClient.addItemInventory(inventoryRequest);
        return InventoryResponse.newBuilder()
                .id(response.getId())
                .message(response.getMessage())
                .build();
    }


    public InventoryResponse updateItemInventory(UpdateInventoryInput input) {
        UpdateInventory updateInventory = InventoryMapper.INSTANCE.mapToGrpcUpdateRequest(input);
        ecommerce.proto_service.grpc.inventory.InventoryResponse response = inventoryClient.updateItemInventory(updateInventory);
        return InventoryResponse.newBuilder()
                .id(response.getId())
                .message(response.getMessage())
                .build();
    }


    public InventoryResponse deleteItemInventory(String id) {
        InventoryId inventoryId = InventoryId.newBuilder().setId(id).build();
        ecommerce.proto_service.grpc.inventory.InventoryResponse response =
                inventoryClient.deleteItemInventory(inventoryId);
        return InventoryResponse.newBuilder()
                .id(response.getId())
                .message(response.getMessage())
                .build();
    }
}
