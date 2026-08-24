package com.example.fullness.stationary.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.example.fullness.stationary.entity.ProjectCategory;

/**
 * プロジェクトカテゴリ情報のデータベースアクセスを担うリポジトリ（MyBatisのマッパー）インターフェース。
 * 
 * @author Team_D
 * @version 1.0
 */
@Mapper
public interface ProjectCategoryRepository {

    /**
     * カテゴリマスタから全カテゴリをカテゴリIDの昇順で取得します。
     * 
     * @return カテゴリID昇順のProjectCategoryのリスト
     */
    List<ProjectCategory> findAllByOrderByCategoryIdAsc();
}