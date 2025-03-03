package ecommerce.bff_service.order_svc.resource;

import com.example.demo.codegen.types.ListOrdersResponse;
import com.example.demo.codegen.types.Order;
import com.netflix.graphql.dgs.DgsComponent;
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
}
