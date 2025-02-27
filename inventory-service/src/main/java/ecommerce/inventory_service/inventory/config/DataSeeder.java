package ecommerce.inventory_service.inventory.config;


import ecommerce.inventory_service.inventory.Inventory;
import ecommerce.inventory_service.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() == 0) {
            seedInventory();
        }
    }

    private void seedInventory() {
        List<Inventory> inventoryList = Arrays.asList(
                createInventory("INV-001", "32939434", 100, 10), // Coffee
                createInventory("INV-002", "8723843", 150, 5),  // Tea Bag
                createInventory("INV-003", "983475", 50, 2),   // Chocolate Cake
                createInventory("INV-004", "784564", 200, 20), // Potato Chips
                createInventory("INV-005", "324576", 75, 8),   // Cheddar Cheese
                createInventory("INV-006", "456789", 60, 3),   // Frozen Chicken
                createInventory("INV-007", "567890", 120, 15), // Shampoo
                createInventory("INV-008", "678901", 90, 7)    // Dishwashing Liquid
        );

        inventoryRepository.saveAll(inventoryList);
        System.out.println("Inventory created successfully");
    }

    private Inventory createInventory(String id, String productId, int quantity, int reserved) {
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        inventory.setReserved(reserved);
        inventory.setUpdateTime(LocalDateTime.now());
        return inventory;
    }
}
