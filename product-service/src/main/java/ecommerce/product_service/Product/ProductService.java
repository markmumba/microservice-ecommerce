package ecommerce.product_service.Product;


import ecommerce.product_service.Product.dto.ProductRequestDto;
import ecommerce.proto_service.grpc.product.ProductItem;
import ecommerce.proto_service.grpc.product.ProductRequest;
import ecommerce.proto_service.grpc.product.ProductResponse;
import ecommerce.proto_service.grpc.product.UpdateRequest;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct (ProductRequest request);
    ProductResponse getProductById (String id);
    List<ProductItem> getAllProducts();
    List<ProductItem> getProductsByCategory(String categoryId);
    String updateProduct (String id, UpdateRequest request);
    String deleteProduct (String id);
}
