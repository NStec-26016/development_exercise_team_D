package com.example.fullness.stationary.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.example.fullness.stationary.entity.Category;

@Mapper
public interface CategoryRepository {
    List<Category> findAllByOrderByCategoryIdAsc();
}