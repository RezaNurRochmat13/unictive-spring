package com.spring.unictive.module.article.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.unictive.module.article.entity.Article;
import com.spring.unictive.module.article.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // uses application-test.properties
public class ArticlePresenterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDB() {
        articleRepository.deleteAll();
    }

    @Test
    void testGetAllArticles() throws Exception {
        Article article = new Article();
        article.setTitle("Test");
        article.setDescription("Test Desc");
        article.setContent("Content");
        article.setAuthor("Reja");

        articleRepository.save(article);

        mockMvc.perform(get("/api/v1/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value("Test"));
    }

    @Test
    void testGetArticleById() throws Exception {
        Article article = new Article();
        article.setTitle("Detail");
        article.setDescription("Detail Desc");
        article.setContent("Detail Content");
        article.setAuthor("Reja");
        Article saved = articleRepository.save(article);

        mockMvc.perform(get("/api/v1/articles/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Detail"));
    }

    @Test
    void testGetArticleById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/articles/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateArticle() throws Exception {
        Article article = new Article();
        article.setTitle("Create");
        article.setDescription("Create Desc");
        article.setContent("Create Content");
        article.setAuthor("Reja");

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Create"));

        List<Article> all = articleRepository.findAll();
        assertEquals(1, all.size());
    }

    @Test
    void testCreateArticle_invalidPayload() throws Exception {
        Article invalidArticle = new Article(); // kosong

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidArticle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateArticle() throws Exception {
        Article article = new Article();
        article.setTitle("Old Title");
        article.setDescription("Old");
        article.setContent("Old Content");
        article.setAuthor("Reja");

        Article saved = articleRepository.save(article);

        Article updated = new Article();
        updated.setTitle("New Title");
        updated.setDescription("New Desc");
        updated.setContent("New Content");
        updated.setAuthor("New Author");

        mockMvc.perform(put("/api/v1/articles/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New Title"));

        Article updatedFromDb = articleRepository.findById(saved.getId()).get();
        assertEquals("New Title", updatedFromDb.getTitle());
    }

    @Test
    void testUpdateArticle_notFound() throws Exception {
        Article update = new Article();
        update.setTitle("X");
        update.setAuthor("Test Author");
        update.setContent("Test content");
        update.setDescription("Test description");

        mockMvc.perform(put("/api/v1/articles/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateArticle_badRequest() throws Exception {
        Article update = new Article();
        update.setTitle("X");

        mockMvc.perform(put("/api/v1/articles/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testDeleteArticle() throws Exception {
        Article article = new Article();
        article.setTitle("To Delete");
        article.setDescription("...");
        article.setContent("...");
        article.setAuthor("Reja");

        Article saved = articleRepository.save(article);

        mockMvc.perform(delete("/api/v1/articles/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        Article softDeleted = articleRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(softDeleted.getDeletedAt());
    }

    @Test
    void testDeleteArticle_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidRouteReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/articlez")) // typo
                .andExpect(status().isNotFound());
    }
}
