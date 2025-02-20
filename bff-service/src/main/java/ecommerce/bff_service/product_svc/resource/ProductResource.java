package ecommerce.bff_service.product_svc.resource;

import com.example.demo.codegen.types.Product;
import com.example.demo.codegen.types.ProductInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import ecommerce.bff_service.product_svc.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author markianmwangi
 * @project ecommerce
 */
@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class ProductResource {
    private final ProductService productService;
    /**
     * get products
     * @return
     * list of products
     */
    @DgsQuery
    public CompletableFuture<List<Product>> getAllProducts () {
        return productService.getAllProducts()
                .thenApply(products -> {
                    log.info(products.toString());
                    return products;
                });
    }

    @DgsMutation
    public Product createProduct (@InputArgument ProductInput input) {
        return productService.createProduct(input);
    }

    @DgsQuery
    public Product getProductById(@InputArgument String id) {
        return productService.getProductById(id);
    }

    @DgsMutation
    public String updateProduct(@InputArgument ProductInput input ) {
        return productService.updateProduct(input);
    }

    @DgsMutation
    public String deleteProduct(@InputArgument String id) {
        return productService.deleteProduct(id);
    }



}

