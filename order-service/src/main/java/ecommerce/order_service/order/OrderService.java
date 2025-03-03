package ecommerce.order_service.order;

import ecommerce.proto_service.grpc.order.*;
import ecommerce.proto_service.grpc.order.Order;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    Order getOrderById(OrderId id);
    ListOrdersResponse getAllOrders();
    ListOrdersResponse getAllOrdersByUser(UserId id);
    Order getOrderByCode(OrderCode code);
    ListOrdersResponse getAllOrdersByDateRange(DateRange dateRange);
    OrderResponse updateRequest(UpdateOrder request);
    OrderResponse deleteOrder(OrderId id);
}
