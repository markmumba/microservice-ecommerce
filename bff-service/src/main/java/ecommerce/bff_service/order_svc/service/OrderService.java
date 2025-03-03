package ecommerce.bff_service.order_svc.service;

import com.example.demo.codegen.types.ListOrdersResponse;
import com.example.demo.codegen.types.Order;
import com.google.protobuf.Empty;
import ecommerce.bff_service.order_svc.mapper.OrderMapper;
import ecommerce.proto_service.grpc.order.OrderId;
import ecommerce.proto_service.grpc.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Order getOrderById (String id) {
        return OrderMapper.INSTANCE.mapOrder(
                orderClient.getOrderById(OrderId.newBuilder().setId(id).build())
        );
    }

}

