package ecommerce.bff_service.order_svc.service;

import com.example.demo.codegen.types.*;
import com.example.demo.codegen.types.Order;
import com.example.demo.codegen.types.OrderResponse;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import ecommerce.bff_service.order_svc.mapper.OrderMapper;
import ecommerce.proto_service.grpc.order.*;
import ecommerce.proto_service.grpc.order.ListOrdersResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderServiceGrpc.OrderServiceStub asyncOrderClient;

    private final OrderServiceGrpc.OrderServiceBlockingStub orderClient;

    public CompletableFuture<List<Order>> getAllOrders() {
        CompletableFuture<List<Order>> future = new CompletableFuture<>();
        List<ecommerce.proto_service.grpc.order.Order> grpcOrderResponses = new ArrayList<>();
        Empty empty = Empty.newBuilder().build();

        asyncOrderClient.getAllOrders(empty, new StreamObserver<ecommerce.proto_service.grpc.order.ListOrdersResponse>() {
            @Override
            public void onNext(ecommerce.proto_service.grpc.order.ListOrdersResponse value) {

                log.info("This is what we get on the orderClient ");
                log.info("*".repeat(30));
                log.info(value.toString());

                grpcOrderResponses.addAll(value.getOrdersList());
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error while fetching all orders");
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                List<Order> dgsOrderResponses = grpcOrderResponses.stream()
                        .map(OrderMapper.INSTANCE::mapOrder)
                        .toList();

                future.complete(dgsOrderResponses);
            }
        });
        return future;
    }

    public Order getOrderById(String id) {
        return OrderMapper.INSTANCE.mapOrder(
                orderClient.getOrderById(OrderId.newBuilder().setId(id).build())
        );
    }

    public CompletableFuture<List<Order>> getOrdersByUser(String id) {
        CompletableFuture<List<Order>> future = new CompletableFuture<>();
        List<ecommerce.proto_service.grpc.order.Order> grpcOrderResponses = new ArrayList<>();


        asyncOrderClient.getOrderByUser(UserId.newBuilder().setId(id).build(), new StreamObserver<ListOrdersResponse>() {
            @Override
            public void onNext(ListOrdersResponse value) {
                grpcOrderResponses.addAll(value.getOrdersList());
            }

            @Override
            public void onError(Throwable t) {
                log.error("error getting the users orders ,{} ", t.getMessage());
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                List<Order> dgsOrderResponse = grpcOrderResponses.stream().map(OrderMapper.INSTANCE::mapOrder).toList();
                future.complete(dgsOrderResponse);
            }
        });

        return future;
    }

    public Order getOrderByCode(String orderCode) {
        return OrderMapper.INSTANCE.mapOrder(
                orderClient.getOrderByCode(OrderCode.newBuilder().setCode(orderCode).build())
        );
    }

    public CompletableFuture<List<Order>> getOrdersByDateRange(DateRangeInput dateRangeInput) {
        CompletableFuture<List<Order>> future = new CompletableFuture<>();

        List<ecommerce.proto_service.grpc.order.Order> grpcResponses = new ArrayList<>();


        DateRange dateRange = DateRange.newBuilder()
                .setStartDate(toTimestamp(dateRangeInput.getStartDate()))
                .setEndDate(toTimestamp(dateRangeInput.getEndDate()))
                .build();

        asyncOrderClient.getOrdersByDateRange(dateRange, new StreamObserver<ListOrdersResponse>() {
            @Override
            public void onNext(ListOrdersResponse value) {
                grpcResponses.addAll(value.getOrdersList());
            }

            @Override
            public void onError(Throwable t) {
                log.error("error getting orders by date range {}",t.getMessage());
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                List<Order> dgsOrderResponse = grpcResponses.stream().map(OrderMapper.INSTANCE::mapOrder).toList();
                future.complete(dgsOrderResponse);
            }
        });

        return future;

    }

    public OrderResponse createOrder(CreateOrderInput input ) {

        CreateOrderRequest request = OrderMapper.INSTANCE.mapOrderRequest(input);
        ecommerce.proto_service.grpc.order.OrderResponse orderResponse = orderClient.createOrder(request);

        return  OrderResponse.newBuilder()
                .orderId(orderResponse.getOrderId())
                .message(orderResponse.getMessage())
                .build();
    }


    public OrderResponse updateOrder(UpdateOrderInput input) {

        UpdateOrder request = OrderMapper.INSTANCE.mapOrderUpdateRequest(input);

        ecommerce.proto_service.grpc.order.OrderResponse orderResponse = orderClient.updateOrder(request);

        return OrderResponse.newBuilder()
                .orderId(orderResponse.getOrderId())
                .message(orderResponse.getMessage())
                .build();

    }

    public OrderResponse deleteOrder (String id ) {
        ecommerce.proto_service.grpc.order.OrderResponse orderResponse = orderClient.deleteOrder(OrderId.newBuilder().setId(id).build());
        return OrderResponse.newBuilder()
                .orderId(orderResponse.getOrderId())
                .message(orderResponse.getMessage())
                .build();
    }


    private Timestamp toTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        // Assuming your input format is ISO-8601 (e.g., "2024-03-03T10:15:30")
        Instant instant = LocalDateTime.parse(dateStr).toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

