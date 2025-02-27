package ecommerce.order_service.order.impl;

import ecommerce.order_service.order.OrderService;
import ecommerce.order_service.order.Order;
import ecommerce.order_service.order.OrderRepository;
import ecommerce.order_service.order.ProductOrder;
import ecommerce.order_service.order.dto.OrderMapper;
import ecommerce.proto_library.utils.Utils;
import ecommerce.proto_service.grpc.order.*;
import ecommerce.proto_service.grpc.product.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author markianmwangi
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceGrpc.ProductServiceBlockingStub productClient;
    private final ProductServiceGrpc.ProductServiceStub asyncProductClient;
    private final OrderProducer orderProducer;
    private Throwable error;

    /**
     * create order uses product id to get price from product service to get total amount
     *
     * @param request contains product id and quantity
     * @return returns the id and success message
     */

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Starting order creation with request: {}", request);

        try {
            LocalDateTime time = LocalDateTime.now();
            log.info("Order timestamp: {}", time);

            // Problem: BigDecimal is initialized with an empty string
            BigDecimal totalAmount = BigDecimal.ZERO;
            log.debug("Initial totalAmount set");

            log.info("Processing {} products from request", request.getProductsCount());
            for (ecommerce.proto_service.grpc.order.ProductOrder productItem : request.getProductsList()) {
                ProductId productId = ProductId.newBuilder().setId(productItem.getProductId()).build();
                log.debug("Requesting product with ID: {}", productId.getId());

                ProductResponse product = productClient.getProductById(productId);
                if (product == null) {
                    log.error("Product not found for ID: {}", productId.getId());
                    throw new IllegalArgumentException("Product not found for ID: " + productId.getId());
                }
                log.debug("Retrieved product: {}", product);

                BigDecimal totalPerProduct = calculateTotal(productItem.getQuantity(), product.getPrice());
                log.debug("Total for product {}: {}", productId.getId(), totalPerProduct);

                totalAmount = totalAmount.add(totalPerProduct);
                log.debug("Running total after adding product {}: {}", productId.getId(), totalAmount);
            }

            log.info("Mapping request to Order entity");
            Order order = orderMapper.requestToEntityOrder(request);
            order.setTotalAmount(totalAmount);
            order.setOrderDate(time);

            String orderCode = Utils.generateCode(6);
            log.debug("Generated order code: {}", orderCode);
            order.setOrderCode(orderCode);

            log.info("Saving order to repository");
            Order savedOrder = orderRepository.save(order);
            log.info("Order saved with ID: {}", savedOrder.getId());

            log.info("Sending created order to message queue");
            orderProducer.sendCreatedOrder(savedOrder.toString());

            OrderResponse response = OrderResponse.newBuilder()
                    .setOrderId(savedOrder.getId())
                    .setMessage("Order created successfully")
                    .build();
            log.info("Returning order response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Error creating order ", e);
        }
    }

    private BigDecimal calculateTotal(int quantity, String price) {
        try {
            return new BigDecimal(quantity).multiply(new BigDecimal(price));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + price, e);
        }
    }


    @Override
    public ecommerce.proto_service.grpc.order.Order getOrderById(OrderId id) {

        Order order = orderRepository.findById(id.getId()).orElseThrow(
                () -> new IllegalArgumentException("order of given id not found " + id.getId())
        );

        ecommerce.proto_service.grpc.order.Order.Builder responseOrderBuilder =
                orderMapper.fromOrderEntityToDto(order).toBuilder();

        List<ProductItem> productItems = new ArrayList<>();

        List<String> productIds = order.getProducts().stream().map(ProductOrder::getProductId).toList();

        ProductIdsList productIdsList = ProductIdsList.newBuilder()
                .addAllId(productIds)
                .build();

        CountDownLatch latch = new CountDownLatch(1);


        asyncProductClient.getProductByIds(productIdsList, new StreamObserver<ProductListResponse>() {
            @Override
            public void onNext(ProductListResponse value) {
                productItems.addAll(value.getProductsList());
            }

            @Override
            public void onError(Throwable t) {
                error=t;
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
            if(error != null) {
                throw new RuntimeException("Error fetching products", error);
            }


            Map<String, ProductItem> productMap = productItems.stream()
                    .collect(
                            Collectors.toMap(ProductItem::getId, productItem -> productItem));

            List<ProductOrderResponse> productResponses = order.getProducts().stream()
                    .map(product -> {
                        ProductItem productItem = productMap.get(product.getProductId());
                        if (productItem == null) {
                            log.warn("Product not found: {}", product.getProductId());
                            return null;
                        }
                        return orderMapper.mapProductToResponse(productItem,product);
                    }).filter(Objects::nonNull)
                    .toList();


            responseOrderBuilder.clearProducts();
            responseOrderBuilder.addAllProducts(productResponses);

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for grpc response ", e);
        }

        return responseOrderBuilder.build();
    }


    @Override
    public ListOrdersResponse getAllOrders() {
        List<ecommerce.proto_service.grpc.order.Order> orders =
                orderRepository.findAll()
                        .stream()
                        .map(orderMapper::fromOrderEntityToDto)
                        .toList();

        return ListOrdersResponse.newBuilder()
                .addAllOrders(orders)
                .build();
    }

    @Override
    public ListOrdersResponse getAllOrdersByUser(UserId id) {
        List<ecommerce.proto_service.grpc.order.Order> orders =
                orderRepository.findAllByUserId(id.getId())
                        .stream()
                        .map(orderMapper::fromOrderEntityToDto)
                        .toList();

        return ListOrdersResponse.newBuilder()
                .addAllOrders(orders)
                .build();
    }

    @Override
    public ListOrdersResponse getAllOrdersByCode(OrderCode code) {
        List<ecommerce.proto_service.grpc.order.Order> orders =
                orderRepository.findAllByOrderCode(code.getCode())
                        .stream()
                        .map(orderMapper::fromOrderEntityToDto)
                        .toList();

        return ListOrdersResponse.newBuilder()
                .addAllOrders(orders)
                .build();
    }

    @Override
    public ListOrdersResponse getAllOrdersByDateRange(DateRange dateRange) {

        LocalDateTime startDate = Instant.ofEpochSecond(
                dateRange.getStartDate().getSeconds(),
                dateRange.getStartDate().getNanos()
        ).atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime endDate = Instant.ofEpochSecond(
                dateRange.getEndDate().getSeconds(),
                dateRange.getEndDate().getNanos()
        ).atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<ecommerce.proto_service.grpc.order.Order> orders =
                orderRepository.findByOrderDateBetween(startDate, endDate)
                        .stream()
                        .map(orderMapper::fromOrderEntityToDto)
                        .toList();

        return ListOrdersResponse.newBuilder()
                .addAllOrders(orders)
                .build();
    }

    /**
     * this the update request where we get the existing order
     * we create a map with the id and the object of the product and quantity
     * we loop through the new product items check if the id exists in the map ...
     * if it exists we check the quantity and update with the new quantity
     * then we calculate the old amount then minus from total then add the new amount
     * finally we add item if it was not in the list
     * then we remove the ones not in the new list
     *
     * @param request a new order request with new product values
     * @return return a new object with the id of the order and the message
     */

    @Override
    public OrderResponse updateRequest(UpdateOrder request) {
        try {
            Order order = orderRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getId()));

            //Here we are setting a map of all existing productOrders id of product and the object itself
            Map<String, ProductOrder> existingProducts = order.getProducts()
                    .stream()
                    .collect(Collectors.toMap(ProductOrder::getProductId, Function.identity()));

            // we get existing total amount
            BigDecimal totalAmount = order.getTotalAmount();

            for (ecommerce.proto_service.grpc.order.ProductOrder newProductOrder : request.getProductsList()) {
                String productId = newProductOrder.getProductId();
                int newQuantity = newProductOrder.getQuantity();

                ProductResponse product = productClient.getProductById(
                        ProductId.newBuilder().setId(productId).build()
                );

                if (product == null) {
                    throw new IllegalArgumentException("Product not found for ID: " + productId);
                }

                if (existingProducts.containsKey(productId)) {
                    ProductOrder existingProductOrder = existingProducts.get(productId);
                    int oldQuantity = existingProductOrder.getQuantity();
                    BigDecimal oldTotal = calculateTotal(oldQuantity, product.getPrice());
                    BigDecimal newTotal = calculateTotal(newQuantity, product.getPrice());

                    totalAmount = totalAmount.subtract(oldTotal).add(newTotal);
                    existingProductOrder.setQuantity(newQuantity);
                } else {
                    ProductOrder newProductOrderToSave = orderMapper.toEntityFromProductOrder(newProductOrder);
                    order.getProducts().add(newProductOrderToSave);
                    totalAmount = totalAmount.add(calculateTotal(newQuantity, product.getPrice()));
                }

            }
            Set<String> newProductIds = request.getProductsList()
                    .stream()
                    .map(ecommerce.proto_service.grpc.order.ProductOrder::getProductId)
                    .collect(Collectors.toSet());

            order.getProducts().removeIf(productOrder -> !newProductIds.contains(productOrder.getProductId()));

            order.setTotalAmount(totalAmount);
            order.setUpdateTime(LocalDateTime.now());
            orderRepository.save(order);

            return OrderResponse.newBuilder()
                    .setOrderId(request.getId())
                    .setMessage("Order has been successfully been updated")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderResponse deleteOrder(OrderId id) {
        Order order = orderRepository.findById(id.getId()).orElseThrow(
                () -> new IllegalArgumentException("order of given id not found " + id.getId()));

        orderRepository.delete(order);

        return OrderResponse.newBuilder()
                .setOrderId(id.getId())
                .setMessage("Order has been deleted successfully")
                .build();
    }
}
