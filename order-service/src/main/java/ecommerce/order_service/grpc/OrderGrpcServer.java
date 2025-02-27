package ecommerce.order_service.grpc;


import com.google.protobuf.Empty;
import ecommerce.order_service.order.OrderService;
import ecommerce.proto_service.grpc.order.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@AllArgsConstructor
public class OrderGrpcServer extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            log.info("*".repeat(30));
            log.info("This is what we get for the request ");
            log.info(request.toString());
            log.info("*".repeat(30));
            OrderResponse response = orderService.createOrder(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void getOrderById(OrderId request, StreamObserver<Order> responseObserver) {
        try {
            Order order = orderService.getOrderById(request);
            responseObserver.onNext(order);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }

    }

    @Override
    public void getAllOrders(Empty request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {
            ListOrdersResponse response = orderService.getAllOrders();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException());
        }
    }

    @Override
    public void getOrderByUser(UserId request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {
            ListOrdersResponse response = orderService.getAllOrdersByUser(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException()
            );

        }
    }

    @Override
    public void getOrdersByCode(OrderCode request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {

            ListOrdersResponse response = orderService.getAllOrdersByCode(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException()
            );
        }
    }

    @Override
    public void getOrdersByDateRange(DateRange request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {

            ListOrdersResponse response = orderService.getAllOrdersByDateRange(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException()
            );
        }
    }


    @Override
    public void updateOrder(UpdateOrder request, StreamObserver<OrderResponse> responseObserver) {
        try {
            // Validate request
            if (request.getId().isEmpty()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT.withDescription(
                                        "Order ID is required for update")
                                .asRuntimeException()
                );
                return;
            }

            OrderResponse response = orderService.updateRequest(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException()
            );
        }
    }

    @Override
    public void deleteOrder(OrderId request, StreamObserver<OrderResponse> responseObserver) {
        try {
            // Validate request
            if (request.getId().isEmpty()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT.withDescription(
                                        "Order ID is required for deletion")
                                .asRuntimeException()
                );
                return;
            }

            OrderResponse response = orderService.deleteOrder(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(
                                    e.getMessage())
                            .withCause(e).asRuntimeException()
            );
        }
    }
}