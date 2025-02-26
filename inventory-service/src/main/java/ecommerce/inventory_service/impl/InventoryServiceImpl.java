package ecommerce.inventory_service.impl;

import ecommerce.inventory_service.InventoryService;
import ecommerce.proto_service.grpc.inventory.*;

import java.util.List;

public class InventoryServiceImpl implements InventoryService {
    @Override
    public InventoryResponse addItemInventory(InventoryRequest request) {
        return null;
    }

    @Override
    public List<Inventory> getAllItemsInventory() {
        return List.of();
    }

    @Override
    public Inventory getItemInventoryById(InventoryId request) {
        return null;
    }

    @Override
    public InventoryResponse updateItemInventory(UpdateInventory request) {
        return null;
    }

    @Override
    public InventoryResponse deleteItemInventory(InventoryId request) {
        return null;
    }
}
