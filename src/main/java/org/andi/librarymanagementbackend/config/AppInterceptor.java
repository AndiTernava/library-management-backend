package org.andi.librarymanagementbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Interceptor për:
 * 1) Kontroll role (vetëm ADMIN mund të bëjë DELETE në çdo api)
 * 2) Shton header-in X-App-Version
 * 3) Logging e kohës së ekzekutimit të çdo kërkese ne console
 * 4) Në rast mospërputhjeje të role, kthen JSON me mesazh të personalizuar
 */
@Component
public class AppInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AppInterceptor.class);
    private static final String START_TIME_ATTR = "startTime";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${app.version}")
    private String appVersion;

    // DTO për error JSON
    private record ApiError(int status, String error, String message) {}

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        // fillojmë matjen e kohës
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        String path   = request.getRequestURI();
        String method = request.getMethod();

        // Vetëm ADMIN mund të bëjë DELETE në çdo /api/**
        if ("DELETE".equals(method) && path.startsWith("/api/")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null &&
                    auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                // Ndërto errorin e personalizuar
                ApiError err = new ApiError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden",
                        "Duhet të jeni ADMIN për të bërë DELETE në API"
                );
                String json = MAPPER.writeValueAsString(err);

                // Kthe 403 me JSON
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                response.getWriter().write(json);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) {
        // Shto header-in me versionin e aplikacionit
        response.addHeader("X-App-Version", appVersion);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Object startObj = request.getAttribute(START_TIME_ATTR);
        if (startObj instanceof Long start) {
            long duration = System.currentTimeMillis() - start;
            log.info("Request [{} {}] ekzekutoi në {} ms",
                    request.getMethod(), request.getRequestURI(), duration);
        }
    }
}
