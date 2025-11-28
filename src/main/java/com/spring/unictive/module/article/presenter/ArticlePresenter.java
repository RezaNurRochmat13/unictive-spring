package com.spring.unictive.module.article.presenter;

import com.spring.unictive.module.article.entity.Article;
import com.spring.unictive.module.article.service.ArticleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Articles", description = "APIs for managing articles")
@RestController
@RequestMapping(value = "/api/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticlePresenter {

    @Autowired
    private ArticleServiceImpl articleService;

    @Operation(
        summary = "Get all articles",
        description = "Retrieves a list of all articles"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved list of articles",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = "{\n  \"status\": \"success\",\n  \"data\": [\n    {\n      \"id\": 1,\n      \"title\": \"Sample Article\",\n      \"description\": \"Sample Description\",\n      \"content\": \"Sample Content\",\n      \"author\": \"John Doe\"\n    }\n  ]\n}"
            )
        )
    )
    @GetMapping
    public Map<String, Object> getAllArticles() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", articleService.findAllArticles());
        return response;
    }

    @Operation(
        summary = "Get article by ID",
        description = "Retrieves a single article by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Article found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"success\",\n  \"data\": {\n    \"id\": 1,\n    \"title\": \"Sample Article\",\n    \"description\": \"Sample Description\",\n    \"content\": \"Sample Content\",\n    \"author\": \"John Doe\"\n  }\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Article not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"error\",\n  \"message\": \"Article not found with id: 999\"\n}"
                )
            )
        )
    })
    @GetMapping("/{id}")
    public Map<String, Object> getArticleById(
            @Parameter(description = "ID of the article to be retrieved", required = true, example = "1")
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", articleService.findArticleById(id));
        return response;
    }

    @Operation(
        summary = "Create a new article",
        description = "Creates a new article with the provided details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Article created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = "{\n  \"status\": \"success\",\n  \"data\": {\n    \"id\": 1,\n    \"title\": \"New Article\",\n    \"description\": \"New Description\",\n    \"content\": \"New Content\",\n    \"author\": \"John Doe\"\n  }\n}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Article object that needs to be added",
        required = true,
        content = @Content(
            schema = @Schema(implementation = Article.class),
            examples = @ExampleObject(
                value = "{\n  \"title\": \"New Article\",\n  \"description\": \"New Description\",\n  \"content\": \"New Content\",\n  \"author\": \"John Doe\"\n}"
            )
        )
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> createArticle(@Valid @RequestBody Article article) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", articleService.createArticle(article));
        return response;
    }

    @Operation(
        summary = "Update an existing article",
        description = "Updates the details of an existing article"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Article updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"success\",\n  \"data\": {\n    \"id\": 1,\n    \"title\": \"Updated Article\",\n    \"description\": \"Updated Description\",\n    \"content\": \"Updated Content\",\n    \"author\": \"John Doe\"\n  }\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Article not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"error\",\n  \"message\": \"Article not found with id: 999\"\n}"
                )
            )
        )
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateArticle(
            @Parameter(description = "ID of the article to be updated", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated article object",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = Article.class),
                    examples = @ExampleObject(
                        value = "{\n  \"title\": \"Updated Article\",\n  \"description\": \"Updated Description\",\n  \"content\": \"Updated Content\",\n  \"author\": \"John Doe\"\n}"
                    )
                )
            )
            @Valid @RequestBody Article article) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", articleService.updateArticle(id, article));
        return response;
    }

    @Operation(
        summary = "Delete an article",
        description = "Deletes an article by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Article deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"success\",\n  \"data\": null\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Article not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\n  \"status\": \"error\",\n  \"message\": \"Article not found with id: 999\"\n}"
                )
            )
        )
    })
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteArticle(
            @Parameter(description = "ID of the article to be deleted", required = true, example = "1")
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        articleService.deleteArticle(id);
        return response;
    }
}
