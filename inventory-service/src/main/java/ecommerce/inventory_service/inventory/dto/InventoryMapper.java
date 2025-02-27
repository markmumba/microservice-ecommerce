package ecommerce.inventory_service.inventory.dto;

import com.google.protobuf.Timestamp;
import ecommerce.inventory_service.inventory.Inventory;
import ecommerce.proto_service.grpc.inventory.InventoryProductItem;
import ecommerce.proto_service.grpc.inventory.InventoryRequest;
import ecommerce.proto_service.grpc.product.ProductResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class InventoryMapper {

    public Inventory fromRequestToEntity (InventoryRequest inventoryRequest) {

        Inventory inventory = new Inventory();
        inventory.setProductId(inventoryRequest.getProductId());
        inventory.setQuantity(inventoryRequest.getQuantity());
        return inventory;
    }

    public ecommerce.proto_service.grpc.inventory.Inventory fromEntityToDto (Inventory inventory) {
        Instant instant = inventory.getUpdateTime().toInstant(ZoneOffset.UTC);
        return ecommerce.proto_service.grpc.inventory.Inventory.newBuilder()
                .setId(inventory.getId())
                .setQuantity(inventory.getQuantity())
                .setReserved(inventory.getReserved())
                .setUpdateTime(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build()
                )
                .build();
    }

    public InventoryProductItem fromProductItemFromProductResponse (ProductResponse productResponse) {
        return InventoryProductItem.newBuilder()
                .setId(productResponse.getId())
                .setName(productResponse.getName())
                .setPrice(productResponse.getPrice())
                .build();
    }
}

