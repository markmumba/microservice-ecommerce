package ecommerce.category_service.Category.Impl;

import ecommerce.category_service.Category.Category;
import ecommerce.category_service.Category.CategoryRepository;
import ecommerce.category_service.Category.dto.CategoryMapper;
import ecommerce.proto_service.grpc.category.CategoryId;
import ecommerce.proto_service.grpc.category.CategoryProductItem;
import ecommerce.proto_service.grpc.category.CategoryRequest;
import ecommerce.proto_service.grpc.category.CategoryResponse;
import ecommerce.proto_service.grpc.product.ProductCategoryId;
import ecommerce.proto_service.grpc.product.ProductItem;
import ecommerce.proto_service.grpc.product.ProductListResponse;
import ecommerce.proto_service.grpc.product.ProductServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


//@ExtendWith(MockitoExtension.class)
//class CategoryServiceImplTest {
//
//    @Mock
//    private ProductServiceGrpc.ProductServiceStub client;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private CategoryMapper categoryMapper;
//
//    @InjectMocks
//    private CategoryServiceImpl categoryService;
//
//
//    private String categoryId;
//    private CategoryRequest request;
//    private Category category;
//    private CategoryResponse expectedResponse;
//    private List<ProductItem> productItems;
//    private List<CategoryProductItem> expectedProductItems;
//    private CategoryResponse expectedResponseWithProducts;
//
//
//    @BeforeEach
//    void setup() {
//
//        categoryId = "category-1";
//
//        request = CategoryRequest.newBuilder()
//                .setName("Test Category")
//                .setDescription("This is a test category")
//                .build();
//
//        category = Category.builder()
//                .id("category-1")
//                .name("Test Category")
//                .description("This is a test category")
//                .build();
//
//        expectedResponse = CategoryResponse.newBuilder()
//                .setId("category-1")
//                .setName("Test Category")
//                .setDescription("This is a test category")
//                .addAllProducts(Collections.emptyList())
//                .build();
//
//        productItems = List.of(
//                ProductItem.newBuilder().setId("product-1").setName("test product").build()
//        );
//        expectedProductItems = List.of(
//                CategoryProductItem.newBuilder().setId("product-1").setName("tea").build(),
//                CategoryProductItem.newBuilder().setId("product-2").setName("coffee").build(),
//                CategoryProductItem.newBuilder().setId("product-3").setName("coco").build()
//        );
//
//        expectedResponseWithProducts = CategoryResponse.newBuilder()
//                .setId("category-1")
//                .setName("Test Category")
//                .setDescription("This is a test category")
//                .addAllProducts(expectedProductItems)
//                .build();
//
//
//        ReflectionTestUtils.setField(categoryService, "productStub", client);
//    }
//
//
//    @Test
//    @DisplayName("testing the create category functionality")
//    void test_create_category() {
//
//        when(categoryMapper.toEntity(request)).thenReturn(category);
//        when(categoryRepository.save(category)).thenReturn(category);
//        when(categoryMapper.fromEntity(category, Collections.emptyList())).thenReturn(expectedResponse);
//        CategoryResponse actualResponse = categoryService.createCategory(request);
//        assertEquals(expectedResponse, actualResponse);
//    }
//
//
//    @Test
//    @DisplayName("test get category by id")
//    void test_getCategory_by_id() {
//
//        Mockito.doAnswer(invocation -> {
//            StreamObserver<ProductListResponse> observer = invocation.getArgument(1);
//            ProductItem product = ProductItem.newBuilder().setId("product-1").setName("test product").build();
//            ProductListResponse response = ProductListResponse.newBuilder().addProducts(product).build();
//
//            observer.onNext(response);
//            observer.onCompleted();
//            return null;
//        }).when(client).getProductsByCategory(any(ProductCategoryId.class),
//                any(StreamObserver.class.asSubclass(StreamObserver.class)));
//
//        when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));
//        when(categoryMapper.fromEntity(category, productItems)).thenReturn(expectedResponse);
//
//        CategoryResponse actualResponse = categoryService.getCategoryById("category-1");
//
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse, actualResponse);
//
//        verify(categoryRepository).findById("category-1");
//        verify(client).getProductsByCategory(any(ProductCategoryId.class),
//                any(StreamObserver.class.asSubclass(StreamObserver.class)));
//        verify(categoryMapper).fromEntity(category, productItems);
//
//    }
//
//    @Test
//    @DisplayName(("test if category is not available"))
//    void test_category_not_available() {
//        when(categoryRepository.findById("category-1")).thenReturn(Optional.empty());
//        assertThrows(IllegalArgumentException.class, () -> {
//            categoryService.getCategoryById("category-1");
//        });
//        verify(categoryRepository).findById("category-1");
//    }
//
//    @Test
//    @DisplayName("test when grpc stream errors out")
//    void test_when_grpc_errors_out() {
//
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//
//        doAnswer(invocationOnMock -> {
//            StreamObserver<ProductListResponse> observer = invocationOnMock.getArgument(1);
//            observer.onError(new RuntimeException("Simulated grpc error"));
//            return null;
//        }).when(client).getProductsByCategory(any(ProductCategoryId.class),
//                any(StreamObserver.class.asSubclass(StreamObserver.class)));
//
//        when(categoryMapper.fromEntity(category, Collections.emptyList())).thenReturn(expectedResponse);
//
//        CategoryResponse actualResponse = categoryService.getCategoryById(categoryId);
//
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse, actualResponse);
//
//        verify(categoryRepository).findById(categoryId);
//        verify(client).getProductsByCategory(any(ProductCategoryId.class),
//                any(StreamObserver.class.asSubclass(StreamObserver.class)));
//        verify(categoryMapper).fromEntity(category, Collections.emptyList());
//    }
//
//    @Test
//    @DisplayName("testing that the client is adding the product in batches")
//    void test_grc_is_adding_in_batches() {
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//
//
//        doAnswer(invocationOnMock -> {
//            StreamObserver<ProductListResponse> observer = invocationOnMock.getArgument(1);
//            ProductListResponse batch1= ProductListResponse.newBuilder()
//                    .addProducts(ProductItem.newBuilder().setId("product-1").setName("tea").build())
//                    .build();
//            observer.onNext(batch1);
//            ProductListResponse batch2= ProductListResponse.newBuilder()
//                    .addProducts(ProductItem.newBuilder().setId("product-2").setName("coffee").build())
//                    .addProducts(ProductItem.newBuilder().setId("product-3").setName("coco").build())
//                    .build();
//            observer.onNext(batch2);
//            observer.onCompleted();
//            return null;
//        }).when(client).getProductsByCategory(any(ProductCategoryId.class),any(StreamObserver.class.asSubclass(StreamObserver.class)));
//
//        when(categoryMapper.fromEntity(category,anyList())).thenReturn(expectedResponseWithProducts);
//
//        CategoryResponse actualResponse = categoryService.getCategoryById(categoryId);
//
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse,actualResponse);
//
//    }
//
//
//    @Test
//    void getAllCategories() {
//    }
//
//    @Test
//    void updateCategory() {
//    }
//
//    @Test
//    void deleteCategory() {
//    }
//}