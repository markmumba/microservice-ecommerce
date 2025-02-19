package ecommerce.product_service.Product.dto;

import ecommerce.product_service.Product.Product;
import ecommerce.proto_service.grpc.product.ProductItem;
import ecommerce.proto_service.grpc.product.ProductRequest;
import ecommerce.proto_service.grpc.product.ProductResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductMapper {

    public Product toEntity  (ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(new BigDecimal(request.getPrice()));
        product.setCategoryId(request.getCategoryId());

        return product;
    }

    public ProductResponse fromEntity (Product product) {

        return ProductResponse.newBuilder()
                .setId(product.getId())
                .setProductCode(product.getProductCode())
                .setName(product.getName())
                .setPrice(product.getPrice().toString())
                .setCategoryId(product.getCategoryId())
                .build();
    }

    public ProductItem fromEntityList(Product product) {

        return ProductItem.newBuilder()
            .setId(product.getId())
            .setName(product.getName())
            .setPrice(product.getPrice().toString())
            .build();
    }



}
