// src/main/java/org/andi/librarymanagementbackend/config/TenantEntityListener.java
package org.andi.librarymanagementbackend.config;

import jakarta.persistence.PrePersist;
import org.andi.librarymanagementbackend.model.TenantAware;

/**
 * JPA entity listener to set tenant ID on entities before persisting.
 */
public class TenantEntityListener {

    /**
     * Set the tenant ID on entities that implement TenantAware before persist.
     *
     * @param entity the entity being persisted
     * @throws IllegalStateException if no tenant ID is available in context
     */
    @PrePersist
    public void setTenantId(Object entity) {
        if (entity instanceof TenantAware) {
            String tenantId = TenantContext.getTenantId();
            if (tenantId != null) {
                ((TenantAware) entity).setTenantId(tenantId);
            } else {
                throw new IllegalStateException("Missing tenant ID in context during persist.");
            }
        }
    }
}
