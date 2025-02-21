package ecommerce.order_service.order.impl;

import ecommerce.order_service.OrderService;
import ecommerce.order_service.order.Order;
import ecommerce.order_service.order.OrderRepository;
import ecommerce.order_service.order.ProductOrder;
import ecommerce.order_service.order.dto.OrderMapper;
import ecommerce.proto_library.utils.Utils;
import ecommerce.proto_service.grpc.order.*;
import ecommerce.proto_service.grpc.product.ProductId;
import ecommerce.proto_service.grpc.product.ProductResponse;
import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * create order uses product id to get price from product service to get total amount
     *
     * @param request contains product id and quantity
     * @return returns the id and success message
     */

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        try {
            LocalDateTime time = LocalDateTime.now();
            BigDecimal totalAmount = new BigDecimal("");

            for (ecommerce.proto_service.grpc.order.ProductOrder productItem : request.getProductsList()) {
                ProductId productId = ProductId.newBuilder().setId(productItem.getProductId()).build();
                ProductResponse product = productClient.getProductById(productId);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found for ID: " + productId.getId());
                }

                BigDecimal totalPerProduct = calculateTotal(productItem.getQuantity(), product.getPrice());
                totalAmount = totalAmount.add(totalPerProduct);
            }

            Order order = orderMapper.requestToEntity(request);
            order.setTotalAmount(totalAmount);
            order.setOrderDate(time);
            order.setOrderCode(Utils.generateCode(6));

            Order savedOrder = orderRepository.save(order);

            return OrderResponse.newBuilder()
                    .setOrderId(savedOrder.getId())
                    .setMessage("Order created successfully")
                    .build();

        } catch (Exception e) {
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
        return orderMapper.toDto(order);
    }

    @Override
    public ListOrdersResponse getAllOrders() {
        List<ecommerce.proto_service.grpc.order.Order> orders =
                orderRepository.findAll()
                        .stream()
                        .map(orderMapper::toDto)
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
                        .map(orderMapper::toDto)
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
                        .map(orderMapper::toDto)
                        .toList();

        return ListOrdersResponse.newBuilder()
                .addAllOrders(orders)
                .build();
    }

    @Override
    public ListOrdersResponse getAllOrdersByDateRange(DateRange dateRange) {
        return null;
    }


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
                    ProductOrder newProductOrderToSave = orderMapper.toEntityProductOrder(newProductOrder);
                    order.getProducts().add(newProductOrderToSave);
                    totalAmount = totalAmount.add(calculateTotal(newQuantity,product.getPrice()));
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
