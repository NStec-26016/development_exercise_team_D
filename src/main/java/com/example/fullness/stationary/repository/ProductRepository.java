// package com.example.fullness.stationary.repository;

// import com.example.fullness.stationary.entity.Product;
// import com.example.fullness.stationary.entity.ProjectCategory; // ←
// ProjectCategoryにする！
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
// import java.util.List;

// @Mapper
// public interface ProductRepository {

// List<Product> findAllWithPaging(@Param("limit") int limit, @Param("offset")
// long offset);

// long countAll();

// List<Product> findByCategoryIdWithPaging(@Param("categoryId") Integer
// categoryId, @Param("limit") int limit,
// @Param("offset") long offset);

// long countByCategoryId(@Param("categoryId") Integer categoryId);

// // 戻り値を ProjectCategory にする
// List<ProjectCategory> findAllCategories();
// }