package ecommerce.order_service.order.dto;

import com.google.protobuf.Timestamp;
import ecommerce.order_service.order.Order;
import ecommerce.order_service.order.ProductOrder;
import ecommerce.proto_service.grpc.order.CreateOrderRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class OrderMapper {
    public Order requestToEntity (CreateOrderRequest orderRequest) {
        Order order = new Order();
        order.setProducts(
                orderRequest
                        .getProductsList()
                        .stream()
                        .map(this::toEntityProductOrder)
                        .toList()
        );
        return order;
    }

    public ProductOrder toEntityProductOrder(ecommerce.proto_service.grpc.order.ProductOrder productOrder) {
        ProductOrder productOrder1 = new ProductOrder();
        productOrder1.setProductId(productOrder.getProductId());
        productOrder1.setQuantity(productOrder.getQuantity());
        return productOrder1;
    }

    public ecommerce.proto_service.grpc.order.ProductOrder toDtoProductOrder(ProductOrder productOrder) {
        return ecommerce.proto_service.grpc.order.ProductOrder.newBuilder()
                .setProductId(productOrder.getProductId())
                .setQuantity(productOrder.getQuantity())
                .build();
    }



    public ecommerce.proto_service.grpc.order.Order toDto (Order order){
        Instant instant = order.getOrderDate().toInstant(ZoneOffset.UTC);
        return ecommerce.proto_service.grpc.order.Order.newBuilder()

                .setId(order.getId())

                .addAllProducts(order.getProducts()
                        .stream()
                        .map(this::toDtoProductOrder)
                        .toList())

                .setOrderCode(order.getOrderCode())

                .setOrderDate(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build())
        .build();
    }


}
