package org.andi.librarymanagementbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to register application interceptors.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppInterceptor appInterceptor;

    /**
     * Constructor for WebMvcConfig.
     *
     * @param appInterceptor the application interceptor to register
     */
    @Autowired
    public WebMvcConfig(AppInterceptor appInterceptor) {
        this.appInterceptor = appInterceptor;
    }

    /**
     * Register interceptors for handling API requests.
     *
     * @param registry the InterceptorRegistry to add interceptors to
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(appInterceptor)
                .addPathPatterns("/api/**")
                .order(1);
    }
}
