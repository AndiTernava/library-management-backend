// src/main/java/org/andi/librarymanagementbackend/config/AppConfig.java
package org.andi.librarymanagementbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for application beans.
 */
@Configuration
public class AppConfig {

    /**
     * Create a BCryptPasswordEncoder bean.
     *
     * @return a BCryptPasswordEncoder for password hashing
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
