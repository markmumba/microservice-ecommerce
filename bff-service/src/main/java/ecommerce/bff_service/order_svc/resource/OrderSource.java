package ecommerce.bff_service.order_svc.resource;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import ecommerce.bff_service.order_svc.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@DgsComponent
@RequiredArgsConstructor
public class OrderSource {

    private final OrderService orderService;

    @DgsQuery
    public CompletableFuture<List<Order>> getAllOrders () {
        return orderService.getAllOrders();
    }
    @DgsQuery
    public Order getOrderById(@InputArgument String id) {

        return orderService.getOrderById(id);
    }
    @DgsQuery
    public CompletableFuture<List<Order>> getOrdersByUser(@InputArgument String userId) {
        return orderService.getOrdersByUser(userId);
    }

    @DgsQuery
    public Order getOrderByCode (@InputArgument String orderCode) {
        return orderService.getOrderByCode(orderCode);
    }

    @DgsQuery
    public CompletableFuture<List<Order>> getOrdersByDateRange (@InputArgument DateRangeInput input){
        return orderService.getOrdersByDateRange(input);
    }

    @DgsMutation
    public OrderResponse createOrder (@InputArgument CreateOrderInput input){
        return orderService.createOrder(input);
    }

    @DgsMutation
    public OrderResponse updateOrder (@InputArgument UpdateOrderInput input) {
        return orderService.updateOrder(input);
    }

    @DgsMutation
    public OrderResponse deleteOrder (@InputArgument String id) {
        return orderService.deleteOrder(id);
    }

}
