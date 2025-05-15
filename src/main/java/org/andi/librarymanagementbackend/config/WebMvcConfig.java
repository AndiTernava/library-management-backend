// src/main/java/org/andi/librarymanagementbackend/config/WebMvcConfig.java
package org.andi.librarymanagementbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppInterceptor appInterceptor;

    @Autowired
    public WebMvcConfig(AppInterceptor appInterceptor) {
        this.appInterceptor = appInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(appInterceptor)
                .addPathPatterns("/api/**") // apliko në krejt /api endpoints
                .order(1);
    }
}
