package ecommerce.bff_service.order_svc.mapper;

import com.example.demo.codegen.types.ListOrdersResponse;
import com.example.demo.codegen.types.Order;
import com.example.demo.codegen.types.OrderProductItem;
import com.example.demo.codegen.types.ProductOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    default Order mapOrder(ecommerce.proto_service.grpc.order.Order grpcOrder) {
        return Order.newBuilder()
                .id(grpcOrder.getId())
                .orderCode(grpcOrder.getOrderCode())
                .products(grpcOrder.getProductsList().stream()
                        .map(product ->
                                ProductOrderResponse.newBuilder().product(
                                                OrderProductItem.newBuilder().
                                                        id(product.getProduct().getId())
                                                        .name(product.getProduct().getName())
                                                        .price(product.getProduct().getPrice())
                                                        .build())
                                        .quantity(product.getQuantity()).
                                        build()).toList())
                .totalAmount(grpcOrder.getTotalAmount())
                .orderDate(grpcOrder.getOrderDate().toString())
                .updateTime(grpcOrder.getUpdateTime().toString())
                .build();
    }

}

