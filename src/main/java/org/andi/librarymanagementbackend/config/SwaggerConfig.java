package org.andi.librarymanagementbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .version("1.0")
                        .contact(new Contact().name("Your Name").email("you@example.com"))
                )
                .components(new Components().addSecuritySchemes("X-Tenant-ID",
                        new SecurityScheme()
                                .type(Type.APIKEY)
                                .in(In.HEADER)
                                .name("X-Tenant-ID")
                ))
                .addSecurityItem(new SecurityRequirement().addList("X-Tenant-ID"));
    }
}
