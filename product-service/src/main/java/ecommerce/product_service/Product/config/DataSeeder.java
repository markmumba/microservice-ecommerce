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
            seedProducts();
        }
    }

    private void seedProducts() {
        Product product1 = new Product();
        product1.setId("32939434");
        product1.setProductCode("4ER55AW");
        product1.setName("Coffee");
        product1.setPrice(new BigDecimal("230.33"));
        product1.setCategoryId("4039094"); // Beverages

        Product product2 = new Product();
        product2.setId("8723843");
        product2.setProductCode("IE903UDI");
        product2.setName("Tea Bag");
        product2.setPrice(new BigDecimal("200.33"));
        product2.setCategoryId("4039094"); // Beverages

        Product product3 = new Product();
        product3.setId("983475");
        product3.setProductCode("SNK001");
        product3.setName("Chocolate Cake");
        product3.setPrice(new BigDecimal("500.00"));
        product3.setCategoryId("490332"); // Snacks

        Product product4 = new Product();
        product4.setId("784564");
        product4.setProductCode("SNK002");
        product4.setName("Potato Chips");
        product4.setPrice(new BigDecimal("150.00"));
        product4.setCategoryId("490332"); // Snacks

        Product product5 = new Product();
        product5.setId("324576");
        product5.setProductCode("DRY003");
        product5.setName("Cheddar Cheese");
        product5.setPrice(new BigDecimal("650.00"));
        product5.setCategoryId("987654"); // Dairy Products

        Product product6 = new Product();
        product6.setId("456789");
        product6.setProductCode("FRZ004");
        product6.setName("Frozen Chicken");
        product6.setPrice(new BigDecimal("1200.00"));
        product6.setCategoryId("123456"); // Frozen Foods

        Product product7 = new Product();
        product7.setId("567890");
        product7.setProductCode("PCL005");
        product7.setName("Shampoo");
        product7.setPrice(new BigDecimal("450.00"));
        product7.setCategoryId("678901"); // Personal Care

        Product product8 = new Product();
        product8.setId("678901");
        product8.setProductCode("HHE006");
        product8.setName("Dishwashing Liquid");
        product8.setPrice(new BigDecimal("250.00"));
        product8.setCategoryId("234567"); // Household Essentials

        productRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5, product6, product7, product8));
        System.out.println("Products created successfully");
    }
}
