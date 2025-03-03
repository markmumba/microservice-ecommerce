package ecommerce.bff_service.order_svc.mapper;

import com.example.demo.codegen.types.*;
import ecommerce.proto_service.grpc.order.CreateOrderRequest;
import ecommerce.proto_service.grpc.order.ProductOrder;
import ecommerce.proto_service.grpc.order.UpdateOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);


    default ProductOrder inputToOrder(ProductOrderInput input) {
        return ProductOrder.newBuilder()
                .setProductId(input.getProductId())
                .setQuantity(input.getQuantity())
                .build();

    }


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


    default CreateOrderRequest mapOrderRequest(CreateOrderInput orderInput) {
        return CreateOrderRequest.newBuilder()
                .addAllProducts(
                        orderInput.getProducts().stream()
                                .map(this::inputToOrder)
                                .toList()
                )
                .build();

    }

    default UpdateOrder mapOrderUpdateRequest(UpdateOrderInput input) {
        return UpdateOrder.newBuilder()
                .setId(input.getId())
                .addAllProducts(
                        input.getProducts().stream()
                                .map(this::inputToOrder)
                                .toList())
                .build();
    }

}

