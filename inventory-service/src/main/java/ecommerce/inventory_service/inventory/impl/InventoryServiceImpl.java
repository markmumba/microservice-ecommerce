package ecommerce.inventory_service.inventory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.inventory_service.inventory.Inventory;
import ecommerce.inventory_service.inventory.InventoryRepository;
import ecommerce.inventory_service.inventory.InventoryService;
import ecommerce.inventory_service.inventory.dto.InventoryMapper;
import ecommerce.inventory_service.inventory.dto.external.Order;
import ecommerce.inventory_service.inventory.dto.external.ProductOrder;
import ecommerce.proto_service.grpc.inventory.*;
import ecommerce.proto_service.grpc.product.ProductId;
import ecommerce.proto_service.grpc.product.ProductResponse;
import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productClient;
    private final ObjectMapper objectMapper;
    private final InventoryProducer inventoryProducerService;


    @Override
    public InventoryResponse addItemInventory(InventoryRequest request) {
        try {
            ProductId productId = ProductId.newBuilder().setId(request.getProductId()).build();

            try {
                ProductResponse productResponse = productClient.getProductById(productId);
                Inventory inventory = inventoryMapper.fromRequestToEntity(request);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryRepository.save(inventory);

                return InventoryResponse.newBuilder()
                        .setId(inventory.getId())
                        .setMessage("Inventory has been created successfully")
                        .build();
            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                    log.error("Product not found: {}", request.getProductId());
                    throw new RuntimeException("Product not found with ID: " + request.getProductId());
                }
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding Item Inventory", e);
        }
    }

    @Override
    public List<ecommerce.proto_service.grpc.inventory.Inventory> getAllItemsInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::fromEntityToDto)
                .toList();
    }


    @Override
    public ecommerce.proto_service.grpc.inventory.Inventory getItemInventoryById(InventoryId request) {
        try {

            Inventory inventory = inventoryRepository.findById(request.getId()).orElseThrow(
                    () -> new NoSuchElementException("Inventory of given id not found")
            );
            ProductId productId = ProductId.newBuilder().setId(inventory.getProductId()).build();
            ecommerce.proto_service.grpc.inventory.Inventory.Builder inventoryBuilder = inventoryMapper.fromEntityToDto(inventory).toBuilder();

            ProductResponse productResponse = productClient.getProductById(productId);
            InventoryProductItem inventoryProductItem = inventoryMapper.fromProductItemFromProductResponse(productResponse);
            inventoryBuilder.clearProduct();
            inventoryBuilder.setProduct(inventoryProductItem);

            return inventoryBuilder.build();

        } catch (Exception e) {
            throw new RuntimeException("Error trying to fetch item inventory");
        }
    }

    @Override
    public InventoryResponse updateItemInventory(UpdateInventory request) {
        try {
            Inventory inventory = inventoryRepository.findById(request.getId()).orElseThrow(
                    () -> new NoSuchElementException("Inventory of given id not found")
            );
            inventory.setQuantity(request.getQuantity());
            inventoryRepository.save(inventory);

            return InventoryResponse.newBuilder()
                    .setMessage("Quantity updated successfully")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error trying to fetch item inventory");
        }
    }

    @Override
    public InventoryResponse deleteItemInventory(InventoryId request) {
        Inventory inventory = inventoryRepository.findById(request.getId()).orElseThrow(
                () -> new NoSuchElementException("Inventory of given id not found")
        );
        inventoryRepository.delete(inventory);
        return InventoryResponse.newBuilder()
                .setMessage("Item Inventory deleted successfully")
                .build();
    }

    @Override
    public void updateItemsInventoryFromOrder(Order order) throws JsonProcessingException {
        Map<String,String> response = new HashMap<>();

        Map<String, Inventory> inventoryMap = inventoryRepository.findAll().stream()
                .collect(Collectors.toMap(Inventory::getId, inventory -> inventory));

        for (ProductOrder product : order.getProducts()) {
            Inventory inventory = inventoryMap.get(product.getProductId());
            if (product.getQuantity() <= inventory.getQuantity()) {

                Integer result = inventory.getQuantity() - product.getQuantity();
                inventory.setReserved(product.getQuantity());
                inventory.setQuantity(result);
                inventoryRepository.save(inventory);

                response.put("product","Order can be fulfilled successfully");
                String responseString= objectMapper.writeValueAsString(response);
                inventoryProducerService.inventoryResponseOrder(responseString);

            } else {
                response.put(product.toString(),"Cannot fulfill order inventory not available");
                String responseString= objectMapper.writeValueAsString(response);
                inventoryProducerService.inventoryResponseOrder(responseString);
            }
        }
    }

}
