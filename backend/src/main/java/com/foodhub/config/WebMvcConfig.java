package com.foodhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * WebMvcConfig
 * Configures CORS mappings and Static Resource locations for serving HTML/CSS/JS
 * and Python-generated Matplotlib analytical charts.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Serve frontend static files
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "file:./frontend/",
                        "file:../frontend/"
                );

        // 2. Serve Python-generated charts directly under /charts/**
        String chartsPath = Paths.get("python-analysis/generated-charts").toAbsolutePath().toUri().toString();
        String chartsParentPath = Paths.get("../python-analysis/generated-charts").toAbsolutePath().toUri().toString();
        
        registry.addResourceHandler("/charts/**")
                .addResourceLocations(chartsPath + "/", chartsParentPath + "/");
    }
}
