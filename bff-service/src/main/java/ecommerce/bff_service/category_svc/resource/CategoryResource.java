package ecommerce.bff_service.category_svc.resource;

import com.example.demo.codegen.types.Category;
import com.example.demo.codegen.types.CategoryInput;
import com.example.demo.codegen.types.CategoryList;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import ecommerce.bff_service.category_svc.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class CategoryResource {

    private final CategoryService categoryService;

    @DgsQuery
    public CompletableFuture<List<CategoryList>> getAllCategories() {
        return categoryService.getAllCategories();
    }
    @DgsQuery
    public Category getCategoryById (@InputArgument String id) {
        return categoryService.getCategoryById(id);
    }

    @DgsMutation
    public Category createCategory(@InputArgument  CategoryInput input) {
        return categoryService.createCategory(input);
    }

    @DgsMutation
    public String updateCategory(@InputArgument CategoryInput input) {
        return categoryService.updateCategory(input);
    }

    @DgsMutation
    public String deleteCategory(@InputArgument String id ) {
        return categoryService.deleteCategory(id);
    }
}
