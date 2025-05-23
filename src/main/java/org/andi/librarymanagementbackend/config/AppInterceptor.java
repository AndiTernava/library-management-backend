// src/main/java/org/andi/librarymanagementbackend/config/AppInterceptor.java
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
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Interceptor for:
 * 1) Role check (only ADMIN can DELETE on APIs)
 * 2) Adds X-App-Version header
 * 3) Logs execution time of each request
 * 4) Returns JSON error on role mismatch
 */
@Component
public class AppInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AppInterceptor.class);
    private static final String START_TIME_ATTR = "startTime";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${app.version}")
    private String appVersion;

    // DTO for JSON error response
    private record ApiError(int status, String error, String message) {}

    /**
     * Pre-handle request: start timer and check ADMIN role for DELETE requests.
     *
     * @param request  HTTP servlet request
     * @param response HTTP servlet response
     * @param handler  chosen handler to execute
     * @return true if processing should continue, false if blocked
     * @throws IOException if writing to the response fails
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("DELETE".equals(method) && path.startsWith("/api/")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                ApiError err = new ApiError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden",
                        "Duhet të jeni ADMIN për të bërë DELETE në API"
                );
                String json = MAPPER.writeValueAsString(err);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                response.getWriter().write(json);
                return false;
            }
        }

        return true;
    }

    /**
     * Post-handle request: add X-App-Version header to response.
     *
     * @param request      HTTP servlet request
     * @param response     HTTP servlet response
     * @param handler      chosen handler
     * @param modelAndView the ModelAndView (if any) returned by the handler
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        response.addHeader("X-App-Version", appVersion);
    }

    /**
     * After request completion: log execution time.
     *
     * @param request  HTTP servlet request
     * @param response HTTP servlet response
     * @param handler  chosen handler
     * @param ex       exception thrown (if any)
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Object startObj = request.getAttribute(START_TIME_ATTR);
        if (startObj instanceof Long start) {
            long duration = System.currentTimeMillis() - start;
            log.info("Request [{} {}] executed in {} ms",
                    request.getMethod(), request.getRequestURI(), duration);
        }
    }
}
