
package com.neb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/uploads/tasks/**")
            .addResourceLocations("file:E:/NEBULYTIX TECHNOLOGIES/task attachments/");

        registry
            .addResourceHandler("/uploads/resumes/**")
            .addResourceLocations("file:E:/NEBULYTIX TECHNOLOGIES/application resumes/");
        
        registry
        .addResourceHandler("/reports/daily/**")
        .addResourceLocations("file:E:/NEBULYTIX TECHNOLOGIES/dailyReports/");
        
        registry
        .addResourceHandler("/uploads/profiles/**")
        .addResourceLocations("file:E:/NEBULYTIX TECHNOLOGIES/profile-pictures/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false);
    }
}
