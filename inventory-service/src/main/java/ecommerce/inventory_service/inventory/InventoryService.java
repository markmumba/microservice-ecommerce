package ecommerce.inventory_service.inventory;

import ecommerce.proto_service.grpc.inventory.InventoryId;
import ecommerce.proto_service.grpc.inventory.InventoryRequest;
import ecommerce.proto_service.grpc.inventory.InventoryResponse;
import ecommerce.proto_service.grpc.inventory.UpdateInventory;

import java.util.List;

public interface InventoryService {
    InventoryResponse addItemInventory(InventoryRequest request) ;
    List<ecommerce.proto_service.grpc.inventory.Inventory> getAllItemsInventory();
   ecommerce.proto_service.grpc.inventory.Inventory getItemInventoryById (InventoryId request);
    InventoryResponse updateItemInventory (UpdateInventory request);
   InventoryResponse  deleteItemInventory (InventoryId request );

}
