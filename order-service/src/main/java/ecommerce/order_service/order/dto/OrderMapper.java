package ecommerce.order_service.order.dto;

import com.google.protobuf.Timestamp;
import ecommerce.order_service.order.Order;
import ecommerce.order_service.order.ProductOrder;
import ecommerce.proto_service.grpc.order.CreateOrderRequest;
import ecommerce.proto_service.grpc.order.OrderProductItem;
import ecommerce.proto_service.grpc.order.ProductOrderResponse;
import ecommerce.proto_service.grpc.product.ProductItem;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class OrderMapper {

    public Order requestToEntityOrder(CreateOrderRequest orderRequest) {
        Order order = new Order();
        order.setProducts(
                orderRequest
                        .getProductsList()
                        .stream()
                        .map(this::toEntityFromProductOrder)
                        .toList()
        );
        return order;
    }

    public ProductOrder toEntityFromProductOrder(ecommerce.proto_service.grpc.order.ProductOrder productOrder) {
        ProductOrder productOrder1 = new ProductOrder();
        productOrder1.setProductId(productOrder.getProductId());
        productOrder1.setQuantity(productOrder.getQuantity());
        return productOrder1;
    }


    public ecommerce.proto_service.grpc.order.Order fromOrderEntityToDto(Order order) {
        Instant instant = order.getOrderDate().toInstant(ZoneOffset.UTC);
        return ecommerce.proto_service.grpc.order.Order.newBuilder()
                .setId(order.getId())
                .setOrderCode(order.getOrderCode())
                .setTotalAmount(Double.parseDouble(order.getTotalAmount().toString()))
                .setOrderDate(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build())
                .setUpdateTime(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build())
                .build();
    }

    public ProductOrderResponse mapProductToResponse(ProductItem product, ProductOrder productOrder) {

        OrderProductItem orderProductItem = OrderProductItem.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice())
                .build();

        return ProductOrderResponse.newBuilder()
                .setProduct(orderProductItem)
                .setQuantity(productOrder.getQuantity())
                .build();
    }
}
