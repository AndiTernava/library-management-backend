package org.andi.librarymanagementbackend.config;

import jakarta.persistence.PrePersist;
import org.andi.librarymanagementbackend.config.TenantContext;
import org.andi.librarymanagementbackend.model.TenantAware;

public class TenantEntityListener {

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
