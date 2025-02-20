package ecommerce.bff_service.category_svc.mapper;

import com.example.demo.codegen.types.Category;
import com.example.demo.codegen.types.CategoryInput;
import com.example.demo.codegen.types.CategoryList;
import com.example.demo.codegen.types.ProductItem;
import ecommerce.proto_service.grpc.category.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryList grpcToDto(CategoryListResponse categoryListResponse);

    ProductItem grpcItemToDtoItem(CategoryProductItem item);

    default List<CategoryList> grpcToDtos(List<CategoryListResponse> grpcCategories) {
        return grpcCategories.stream()
                .map(this::grpcToDto)
                .toList();
    }

    default Category grpcCategoryToDtoCategory(CategoryResponse grpcCategory) {

        return Category.newBuilder()
                .id(grpcCategory.getId())
                .name(grpcCategory.getName())
                .description(grpcCategory.getDescription())
                .products(grpcCategory
                        .getProductsList().stream()
                        .map(this::grpcItemToDtoItem)
                        .toList())
                .build();
    }

    default CategoryRequest dtoCategoryReqToGrpcReq(CategoryInput input){

        return CategoryRequest.newBuilder()
                .setName(input.getName())
                .setDescription(input.getDescription())
                .build();
    }

    default UpdateCategory dtUpdateReqToGrpcReq(CategoryInput input){
        return UpdateCategory.newBuilder()
                .setId(input.getId())
                .setName(input.getName())
                .setDescription(input.getDescription())
                .build();
    }
}
