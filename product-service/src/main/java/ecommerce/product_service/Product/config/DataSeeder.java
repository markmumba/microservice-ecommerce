package ecommerce.product_service.Product.config;

import ecommerce.product_service.Product.Product;
import ecommerce.product_service.Product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            seedProduct();
        }
    }

    private void seedProduct () {
        Product product1 = new Product();
        product1.setId("32939434");
        product1.setProductCode("4ER55AW");
        product1.setName("Coffee");
        product1.setPrice(new BigDecimal("230.33"));
        product1.setCategoryId("4039094");

        Product product2 = new Product();
        product2.setId("8723843");
        product2.setProductCode("IE903UDI");
        product2.setName("Tea bag");
        product2.setPrice(new BigDecimal("200.33"));
        product2.setCategoryId("4039094");

        productRepository.saveAll(Arrays.asList(product1,product2));
        System.out.println("product created successfully");
    }
}
