package ecommerce.bff_service.inventory_svc.resource;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import ecommerce.bff_service.inventory_svc.service.InventoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
@RequiredArgsConstructor
public class InventorySource {

    private final InventoryService inventoryService;

    @DgsQuery
    public CompletableFuture<List<Inventory>> getAllItemsInventory () {
       return inventoryService.getAllItemInventory();
    }

    @DgsQuery
    public Inventory getItemInventoryById (@InputArgument  String id) {
        return inventoryService.getItemInventoryById(id);
    }

    @DgsMutation
    public InventoryResponse addItemInventory (@InputArgument InventoryInput input) {
        return inventoryService.addItemInventory(input);
    }

    @DgsMutation
    public InventoryResponse updateItemInventory (@InputArgument UpdateInventoryInput input) {
       return inventoryService.updateItemInventory(input);
    }

    @DgsMutation
    public InventoryResponse deleteItemInventory(@InputArgument String id) {
        return inventoryService.deleteItemInventory(id);

    }
}
