package ecommerce.inventory_service.inventory.impl;

import ecommerce.inventory_service.inventory.Inventory;
import ecommerce.inventory_service.inventory.InventoryRepository;
import ecommerce.inventory_service.inventory.InventoryService;
import ecommerce.inventory_service.inventory.dto.InventoryMapper;
import ecommerce.inventory_service.inventory.dto.external.Order;
import ecommerce.inventory_service.inventory.dto.external.ProductOrder;
import ecommerce.proto_service.grpc.inventory.*;
import ecommerce.proto_service.grpc.order.ProductOrderResponse;
import ecommerce.proto_service.grpc.product.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productClient;
    private final ProductServiceGrpc.ProductServiceStub asyncProductClient;
    private Throwable error;


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

    /**
     * Getting all inventory with the product details
     * @return
     * @throws InterruptedException
     */

    @Override
    public List<ecommerce.proto_service.grpc.inventory.Inventory> getAllItemsInventory() throws InterruptedException {
        //first we get all the inventory from the database this has only the product id
        List<Inventory> inventoryList = inventoryRepository.findAll();

        // We create an empty array of what we will be getting a list of products needed
        List<ProductItem> productItems = new ArrayList<>();

        // We get all the products needed in a batch  so we compile all the product ids together
        List<String> productIds = inventoryList.stream()
                .map(Inventory::getProductId)
                .toList();

        //prepare for the grpc client
        ProductIdsList productIdsList = ProductIdsList.newBuilder().addAllId(productIds).build();

        CountDownLatch latch = new CountDownLatch(1);

        // get the products of the given product ids asynchronously
        asyncProductClient.getProductByIds(productIdsList, new StreamObserver<ProductListResponse>() {
            @Override
            public void onNext(ProductListResponse value) {
                productItems.addAll(value.getProductsList());
            }
            @Override
            public void onError(Throwable t) {
                error = t;
                latch.countDown();
            }
            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for product response");
            }
            if (error != null) {
                throw new RuntimeException("Error fetching products", error);
            }
            //Then we setup a map that has the product id and the product object for easier lookup
            Map<String, ProductItem> productMap = productItems.stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            //Now we create the response inventory with the product details
            //We loop through the inventory list then for each we create a dto which is also a builder
            //Then we  get the product item for that inventory from the map
            //Lastly we set the product for that inventory

            return inventoryList.stream()
                    .map( inventory -> {

                        ecommerce.proto_service.grpc.inventory.Inventory.Builder grpcInventory =
                                inventoryMapper.fromEntityToDto(inventory).toBuilder();

                        ProductItem productItem = productMap.getOrDefault(
                                inventory.getProductId(),
                                createDefaultProductItem(inventory.getProductId()));

                        grpcInventory.setProduct(
                            InventoryProductItem.newBuilder()
                                    .setId(productItem.getId())
                                    .setName(productItem.getName())
                                    .setPrice(productItem.getPrice())
                                    .build());
                        return grpcInventory.build();
                    }).toList();
        }catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for grpc response ", e);
        }
    }

    private ProductItem createDefaultProductItem(String id) {
        return ProductItem.newBuilder()
                .setId(id)
                .setName("Unknown Product")
                .setPrice("0")
                .build();
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
    public void updateItemsInventoryFromOrder(Order order) {

        Map<String, Inventory> inventoryMap = inventoryRepository.findAll().stream()
                .collect(Collectors.toMap(Inventory::getId, inventory -> inventory));

        for (ProductOrder product : order.getProducts()) {
            Inventory inventory = inventoryMap.get(product.getProductId());
            Integer result = inventory.getQuantity() - product.getQuantity();
            inventory.setReserved(product.getQuantity());
            inventory.setQuantity(result);
            inventoryRepository.save(inventory);
        }
    }

}
