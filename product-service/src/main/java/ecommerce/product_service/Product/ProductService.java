package ecommerce.product_service.Product;


import ecommerce.proto_service.grpc.product.*;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct (ProductRequest request);
    ProductResponse getProductById (String id);
    List<ProductItem> getProductsByIds(ProductIdsList request);
    List<ProductItem> getAllProducts();
    List<ProductItem> getProductsByCategory(String categoryId);
    String updateProduct (String id, UpdateRequest request);
    String deleteProduct (String id);
}
