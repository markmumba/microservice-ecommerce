package ecommerce.bff_service.product_svc.mapper;

import com.example.demo.codegen.types.Product;
import com.example.demo.codegen.types.ProductInput;
import ecommerce.proto_service.grpc.product.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    /**
     * Gets the grpc response and converts to java object for grpc
     * @param response
     * this is what we get from the grpc client
     * @return
     * returns the java object for dgs to consume
     */

    Product grpcToDtos (ProductItem response);

    Product grpcToDto (ProductResponse response);

    default List<Product> grpcToDtos(List<ProductItem> productItems) {
        return productItems.stream()
                .map(this::grpcToDtos)
                .toList();
    }

    ProductRequest dtoToGrpc (ProductInput input);

    UpdateRequest updateDtoToGrpcUpdate(ProductInput input);



    default String messageGrpcToDtoMessage(MessageResponse response) {
        return response.getResponse();
    };



}
