package ecommerce.product_service.Product;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository  extends MongoRepository<Product,String> {

    List<Product> findAllByCategoryId(String categoryId);
}
