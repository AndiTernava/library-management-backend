package org.andi.librarymanagementbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LibraryManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementBackendApplication.class, args);
    }

}
