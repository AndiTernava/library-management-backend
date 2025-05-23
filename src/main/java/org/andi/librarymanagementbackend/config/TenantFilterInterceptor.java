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

/**
 * Filter to enforce tenant ID presence and apply Hibernate tenant filter.
 */
@Component
public class TenantFilterInterceptor extends OncePerRequestFilter {

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> EXCLUDE_URL_PATTERNS = List.of(
            "/swagger-ui", "/swagger-ui.html", "/swagger-ui/index.html",
            "/v3/api-docs", "/v3/api-docs/", "/swagger-resources", "/webjars/"
    );

    /**
     * Determine if this filter should be skipped for the current request.
     *
     * @param request the HTTP servlet request
     * @return true if the filter should not be applied, false otherwise
     * @throws ServletException if an error occurs
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDE_URL_PATTERNS.stream().anyMatch(path::startsWith);
    }

    /**
     * Apply tenant context and enable Hibernate filter for tenant isolation.
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenantId = request.getHeader("X-Tenant-ID");
            if (tenantId == null || tenantId.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-ID header");
                return;
            }

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
