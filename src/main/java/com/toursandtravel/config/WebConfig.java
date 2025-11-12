package com.toursandtravel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve uploaded files from the uploads folder
        // Use both classpath (for development) and file system (for runtime uploads)
        String uploadPath = Paths.get("src", "main", "resources", "static", "uploads").toAbsolutePath().toString().replace("\\", "/");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/", "file:" + uploadPath + "/")
                .setCachePeriod(0); // Disable caching for uploaded files
    }
}

