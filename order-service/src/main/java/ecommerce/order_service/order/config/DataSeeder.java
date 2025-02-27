package ecommerce.order_service.order.config;

import ecommerce.order_service.order.Order;
import ecommerce.order_service.order.OrderRepository;
import ecommerce.order_service.order.ProductOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        if (orderRepository.count() == 0) {
            seedOrders();
        }
    }

    private void seedOrders() {
        Order order1 = new Order();
        order1.setId("ORD12345");
        order1.setUserId("USER001");
        order1.setOrderCode("ORDER-001");
        order1.setOrderDate(LocalDateTime.now());
        order1.setUpdateTime(LocalDateTime.now());

        List<ProductOrder> products1 = Arrays.asList(
                new ProductOrder("32939434", 2),  // Coffee
                new ProductOrder("983475", 1)   // Chocolate Cake
        );

        order1.setProducts(products1);
        order1.setTotalAmount(calculateTotal(products1));

        Order order2 = new Order();
        order2.setId("ORD67890");
        order2.setUserId("USER002");
        order2.setOrderCode("ORDER-002");
        order2.setOrderDate(LocalDateTime.now());
        order2.setUpdateTime(LocalDateTime.now());

        List<ProductOrder> products2 = Arrays.asList(
                new ProductOrder("8723843", 3),  // Tea Bag
                new ProductOrder("324576", 2),  // Cheddar Cheese
                new ProductOrder("567890", 1)   // Shampoo
        );

        order2.setProducts(products2);
        order2.setTotalAmount(calculateTotal(products2));

        orderRepository.saveAll(Arrays.asList(order1, order2));
        System.out.println("Orders created successfully");
    }

    private BigDecimal calculateTotal(List<ProductOrder> productOrders) {
        Map<String, BigDecimal> productPrices = Map.of(
                "32939434", new BigDecimal("230.33"), // Coffee
                "8723843", new BigDecimal("200.33"), // Tea Bag
                "983475", new BigDecimal("500.00"), // Chocolate Cake
                "324576", new BigDecimal("650.00"), // Cheddar Cheese
                "567890", new BigDecimal("450.00")  // Shampoo
        );

        return productOrders.stream()
                .map(po -> productPrices.get(po.getProductId()).multiply(BigDecimal.valueOf(po.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
