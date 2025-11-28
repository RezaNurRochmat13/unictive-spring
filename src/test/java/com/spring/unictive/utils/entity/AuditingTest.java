package com.spring.unictive.utils.entity;

import com.spring.unictive.module.article.entity.Article;
import com.spring.unictive.module.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // pakai H2
@ActiveProfiles("test")
public class AuditingTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void testAuditingFields_areSet() {
        Article article = new Article();
        article.setTitle("Audit Test");
        article.setAuthor("Reja");

        Article saved = articleRepository.save(article);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertNull(saved.getDeletedAt());

        System.out.println("Created at: " + saved.getCreatedAt());
        System.out.println("Updated at: " + saved.getUpdatedAt());
    }
}

