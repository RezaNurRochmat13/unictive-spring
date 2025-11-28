package com.spring.unictive.module.article.service;

import com.spring.unictive.module.article.entity.Article;

import java.util.List;

public interface ArticleService {
    List<Article> findAllArticles();
    Article findArticleById(Long id);
    Article createArticle(Article article);
    Article updateArticle(Long id, Article article);
    void deleteArticle(Long id);
}
