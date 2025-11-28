package com.spring.unictive.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    private String getServerUrl() {
        String baseUrl = "http://localhost:" + serverPort;
        if (contextPath != null && !contextPath.isEmpty()) {
            baseUrl += contextPath;
        }
        return baseUrl;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*");
            }
        };
    }

    @Bean
    public OpenAPI myOpenAPI() {
        Server server = new Server();
        server.setUrl(getServerUrl());
        server.setDescription("Local development server");

        Contact contact = new Contact();
        contact.setEmail("your-email@example.com");
        contact.setName("Your Name");
        contact.setUrl("https://your-website.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Spring Boot Boilerplate API")
                .version("1.0.0")
                .contact(contact)
                .description("This API exposes endpoints for the Spring Boot Boilerplate application.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
