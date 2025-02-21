package ecommerce.order_service;

import ecommerce.proto_service.grpc.order.*;
import ecommerce.proto_service.grpc.product.UpdateRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    Order getOrderById(OrderId id);
    ListOrdersResponse getAllOrders();
    ListOrdersResponse getAllOrdersByUser(UserId id);
    ListOrdersResponse getAllOrdersByCode(OrderCode code);
    ListOrdersResponse getAllOrdersByDateRange(DateRange dateRange);
    OrderResponse updateRequest(UpdateOrder request);
    OrderResponse deleteOrder(OrderId id);
}
