package ecommerce.product_service.Product.impl;

import ecommerce.product_service.Product.Product;
import ecommerce.product_service.Product.ProductRepository;
import ecommerce.product_service.Product.ProductService;
import ecommerce.product_service.Product.dto.ProductMapper;
import ecommerce.proto_library.utils.Utils;
import ecommerce.proto_service.grpc.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;



    @Override
    public ProductResponse createProduct(ProductRequest request) {

        String code = Utils.generateCode(6);
        Product product = productMapper.toEntity(request);
        product.setProductCode(code);
        Product savedProduct = productRepository.save(product);

        return productMapper.fromEntity(savedProduct);
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Error finding product of id " + id)
        );
        return productMapper.fromEntity(product);
    }

    @Override
    public List<ProductItem> getProductsByIds(ProductIdsList request) {
        List<String> productIds= request.getIdList();
        return productRepository.findAllById(productIds)
               .stream()
                .map(productMapper::fromEntityList)
                .toList();
    }

    @Override
    public List<ProductItem> getAllProducts() {

        log.info("*".repeat(30));
        log.info("This is the product microservices : the get all products service");
        log.info("*".repeat(30));
        log.info("This is what we return ");

        log.info( productRepository.findAll().stream()
                .map(productMapper::fromEntityList)
                .toList().toString());

        return productRepository.findAll().stream()
                .map(productMapper::fromEntityList)
                .toList();
    }

    @Override
    public List<ProductItem> getProductsByCategory(String categoryId) {
        return productRepository.findAllByCategoryId(categoryId)
                .stream()
                .map(productMapper::fromEntityList)
                .toList();
    }

    @Override
    public String updateProduct(String id, UpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Error finding product of id " + id)
        );

        if(!request.getName().isEmpty()){
            product.setName(request.getName());
        }
        if(!request.getPrice().isEmpty()){
            product.setPrice(new BigDecimal(request.getPrice()));
        }
        productRepository.save(product);
        return "Product updated successfully";
    }

    @Override
    public String deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Error finding product of id " + id)
        );
        if(product != null) {
            productRepository.delete(product);
        }
        return "Product deleted successfully";
    }
}
