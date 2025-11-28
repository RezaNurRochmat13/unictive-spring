package com.spring.unictive.module.article.repository;

import com.spring.unictive.module.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("select a from Article a where a.deletedAt is null")
    List<Article> findAllActiveArticles();
}
