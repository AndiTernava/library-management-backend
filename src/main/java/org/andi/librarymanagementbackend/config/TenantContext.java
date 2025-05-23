// src/main/java/org/andi/librarymanagementbackend/config/TenantContext.java
package org.andi.librarymanagementbackend.config;

/**
 * Thread-local context for storing the current tenant ID for multi-tenancy support.
 */
public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Set the tenant ID for the current thread.
     *
     * @param tenantId the tenant identifier
     */
    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Get the tenant ID for the current thread.
     *
     * @return the tenant ID, or null if not set
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * Clear the tenant ID from the current thread context.
     */
    public static void clear() {
        currentTenant.remove();
    }

    /**
     * Get the tenant ID or throw if not set.
     *
     * @return the tenant ID
     * @throws IllegalStateException if no tenant ID is set
     */
    public static String getOrThrow() {
        String tenantId = getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not set in TenantContext.");
        }
        return tenantId;
    }
}
