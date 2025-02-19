package ecommerce.category_service.Category.config;

import ecommerce.category_service.Category.Category;
import ecommerce.category_service.Category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            seedProduct();
        }
    }

    private void seedProduct () {
        Category category1 = new Category();
        category1.setId("4039094");
        category1.setName("Beverages");
        category1.setDescription("Contains all beverages that are tea based");

        Category category2= new Category();
        category2.setId("490332");
        category2.setName("Snacks");
        category2.setDescription("Here you can find all types of cakes  ");

        categoryRepository.saveAll(Arrays.asList(category1,category2));
        System.out.println("Category created successfully");
    }
}
