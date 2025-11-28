package com.spring.unictive.module.article.service;

import com.spring.unictive.module.article.entity.Article;
import com.spring.unictive.module.article.repository.ArticleRepository;
import com.spring.unictive.utils.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article dummyArticle;

    @BeforeEach
    void setUp() {
        dummyArticle = new Article();
        dummyArticle.setId(1L);
        dummyArticle.setTitle("Test Title");
        dummyArticle.setDescription("Test Description");
        dummyArticle.setContent("Test Content");
        dummyArticle.setAuthor("Reja");
    }

    @Test
    void testFindAllArticles() {
        when(articleRepository.findAllActiveArticles()).thenReturn(List.of(dummyArticle));

        List<Article> articles = articleService.findAllArticles();

        assertEquals(1, articles.size());
        assertEquals("Test Title", articles.get(0).getTitle());
        verify(articleRepository, times(1)).findAllActiveArticles();
    }

    @Test
    void testFindArticleById_found() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(dummyArticle));

        Article result = articleService.findArticleById(1L);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void testFindArticleById_notFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> articleService.findArticleById(1L));
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateArticle() {
        when(articleRepository.save(dummyArticle)).thenReturn(dummyArticle);

        Article result = articleService.createArticle(dummyArticle);

        assertEquals("Test Title", result.getTitle());
        verify(articleRepository, times(1)).save(dummyArticle);
    }

    @Test
    void testCreateArticle_withEmptyTitle_shouldThrowException() {
        dummyArticle.setTitle("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            articleService.createArticle(dummyArticle);
        });

        assertEquals("Title must not be empty", ex.getMessage());
    }

    @Test
    void testCreateArticle_withNullContent_shouldThrowException() {
        dummyArticle.setAuthor(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            articleService.createArticle(dummyArticle);
        });

        assertEquals("Author must not be empty", ex.getMessage());
    }

    @Test
    void testCreateArticle_withLongTitle_shouldThrowException() {
        dummyArticle.setTitle("A".repeat(300)); // Misalnya max 255 karakter

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            articleService.createArticle(dummyArticle);
        });

        assertEquals("Title must not exceed 255 characters", ex.getMessage());
    }


    @Test
    void testUpdateArticle_found() {
        Article updated = new Article();
        updated.setTitle("Updated Title");
        updated.setDescription("Updated Desc");
        updated.setContent("Updated Content");
        updated.setAuthor("New Author");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(dummyArticle));
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        Article result = articleService.updateArticle(1L, updated);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void testUpdateArticle_notFound() {
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        Article dummyUpdate = new Article();
        dummyUpdate.setTitle("Update");

        assertThrows(ResourceNotFound.class, () -> articleService.updateArticle(99L, dummyUpdate));
    }

    @Test
    void testDeleteArticle_found() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(dummyArticle));
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        articleService.deleteArticle(1L);

        assertNotNull(dummyArticle.getDeletedAt());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(dummyArticle);
    }

    @Test
    void testDeleteArticle_notFound() {
        when(articleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> articleService.deleteArticle(2L));
        verify(articleRepository, times(1)).findById(2L);
    }
}
