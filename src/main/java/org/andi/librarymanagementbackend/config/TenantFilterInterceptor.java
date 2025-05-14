package org.andi.librarymanagementbackend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TenantFilterInterceptor extends OncePerRequestFilter {

    @PersistenceContext
    private EntityManager entityManager;

    // Vendos këtu të gjitha path-at që duhen përjashtuar
    private static final List<String> EXCLUDE_URL_PATTERNS = List.of(
            "/swagger-ui",          // Swagger UI v2/v3
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs",         // OpenAPI JSON
            "/v3/api-docs/",
            "/swagger-resources",
            "/webjars/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Nëse path fillon me ndonjërin prej modeleve, mos e aplikoj filter-in
        return EXCLUDE_URL_PATTERNS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenantId = request.getHeader("X-Tenant-ID");

            // Kontrollo që tenantId të jetë i pranishëm, vetëm pasi jemi siguruar
            // që URI nuk është në listën e përjashtimeve
            if (tenantId == null || tenantId.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-ID header");
                return;
            }

            // Vendos context dhe aktivizo filter-in Hibernate
            TenantContext.setTenantId(tenantId);
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenantId", tenantId);

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

