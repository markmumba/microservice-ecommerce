package ecommerce.order_service.grpc;


import com.google.protobuf.Empty;
import ecommerce.order_service.OrderService;
import ecommerce.proto_service.grpc.order.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@AllArgsConstructor
public class OrderGrpcServer extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;
    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderResponse response = orderService.createOrder(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }catch (Exception e){
           responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void getOrderById(OrderId request, StreamObserver<Order> responseObserver) {
        super.getOrderById(request, responseObserver);
    }

    @Override
    public void getAllOrders(Empty request, StreamObserver<ListOrdersResponse> responseObserver) {
        super.getAllOrders(request, responseObserver);
    }

    @Override
    public void getOrderByUser(UserId request, StreamObserver<ListOrdersResponse> responseObserver) {
        super.getOrderByUser(request, responseObserver);
    }

    @Override
    public void getOrdersByCode(OrderCode request, StreamObserver<ListOrdersResponse> responseObserver) {
        super.getOrdersByCode(request, responseObserver);
    }

    @Override
    public void getOrdersByDateRange(DateRange request, StreamObserver<ListOrdersResponse> responseObserver) {
        super.getOrdersByDateRange(request, responseObserver);
    }

    @Override
    public void updateOrder(UpdateOrder request, StreamObserver<OrderResponse> responseObserver) {
        super.updateOrder(request, responseObserver);
    }

    @Override
    public void deleteOrder(OrderId request, StreamObserver<OrderResponse> responseObserver) {
        super.deleteOrder(request, responseObserver);
    }
}