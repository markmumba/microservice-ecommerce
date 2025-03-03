package ecommerce.bff_service.inventory_svc.mapper;

import com.example.demo.codegen.types.Inventory;
import com.example.demo.codegen.types.InventoryInput;
import com.example.demo.codegen.types.InventoryProductItem;
import com.example.demo.codegen.types.UpdateInventoryInput;
import ecommerce.proto_service.grpc.inventory.InventoryRequest;
import ecommerce.proto_service.grpc.inventory.UpdateInventory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface InventoryMapper {
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    default InventoryRequest mapToGrpcRequest(InventoryInput input) {
        return InventoryRequest.newBuilder()
                .setProductId(input.getProductId())
                .setQuantity(input.getQuantity())
                .build();
    }

    default UpdateInventory mapToGrpcUpdateRequest(UpdateInventoryInput input) {
        return UpdateInventory.newBuilder()
                .setId(input.getId())
                .setProductId(input.getProductId())
                .setQuantity(input.getQuantity())
                .build();
    }

    default Inventory mapToDgs(ecommerce.proto_service.grpc.inventory.Inventory grpcInventory) {
        return Inventory.newBuilder()
                .id(grpcInventory.getId())
                .product(InventoryProductItem.newBuilder()
                        .id(grpcInventory.getProduct().getId())
                        .name(grpcInventory.getProduct().getName())
                        .price(grpcInventory.getProduct().getPrice())
                        .build())
                .quantity(grpcInventory.getQuantity())
                .reserved(grpcInventory.getReserved())
                .updateTime(grpcInventory.getUpdateTime().toString())
                .build();
    }
}
