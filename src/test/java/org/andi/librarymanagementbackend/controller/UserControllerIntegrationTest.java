package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.config.TenantContext;
import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.config.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String adminToken;
    private final String tenantId = "test-tenant";

    @BeforeEach
    void setTenantContext() {
        TenantContext.setTenantId(tenantId);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @BeforeEach
    public void setUp() {
        TenantContext.setTenantId(tenantId); // ✅ Ensure tenant is set first

        userRepo.deleteAll();

        User admin = new User("Admin User", "admin@example.com", passwordEncoder.encode("admin123"), User.Role.ADMIN);
        admin = userRepo.save(admin);

        adminToken = jwtUtil.generateToken(admin.getEmail());
    }

    @Test
    void shouldGetAllUsers() {
        // Arrange
        UserDto user1 = new UserDto();
        user1.setFullName("John");
        user1.setEmail("john@example.com");
        user1.setPassword("123");
        user1.setRole(User.Role.MEMBER);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.set("X-Tenant-ID", tenantId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserDto> request = new HttpEntity<>(user1, headers);
        restTemplate.postForEntity(getUrl("/api/users"), request, UserDto.class);

        // Act
        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                getUrl("/api/users"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getEmail()).isEqualTo("john@example.com");
    }

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
