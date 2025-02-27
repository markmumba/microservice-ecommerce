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
            seedCategories();
        }
    }

    private void seedCategories() {
        Category category1 = new Category();
        category1.setId("4039094");
        category1.setName("Beverages");
        category1.setDescription("Contains all beverages that are tea-based.");

        Category category2 = new Category();
        category2.setId("490332");
        category2.setName("Snacks");
        category2.setDescription("Here you can find all types of cakes and quick bites.");

        Category category3 = new Category();
        category3.setId("987654");
        category3.setName("Dairy Products");
        category3.setDescription("Milk, cheese, yogurt, and other dairy-based products.");

        Category category4 = new Category();
        category4.setId("123456");
        category4.setName("Frozen Foods");
        category4.setDescription("Includes frozen meats, vegetables, and ready-to-eat meals.");

        Category category5 = new Category();
        category5.setId("678901");
        category5.setName("Personal Care");
        category5.setDescription("Shampoos, soaps, skincare, and hygiene products.");

        Category category6 = new Category();
        category6.setId("234567");
        category6.setName("Household Essentials");
        category6.setDescription("Cleaning supplies, kitchen utilities, and household goods.");

        categoryRepository.saveAll(Arrays.asList(category1, category2, category3, category4, category5, category6));
        System.out.println("Categories created successfully");
    }
}
