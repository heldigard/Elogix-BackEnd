package com.elogix.api.users.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EPermission {
    SUPER_ADMIN_READ("super_admin:read"),
    SUPER_ADMIN_UPDATE("super_admin:update"),
    SUPER_ADMIN_DELETE("super_admin:delete"),
    SUPER_ADMIN_CREATE("super_admin:create"),
    SUPER_ADMIN_ALL("super_admin:*"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    ADMIN_CREATE("admin:create"),
    ADMIN_ALL("admin:*"),
    MANAGER_READ("manager:read"),
    MANAGER_UPDATE("manager:update"),
    MANAGER_DELETE("manager:delete"),
    MANAGER_CREATE("manager:create"),
    MANAGER_ALL("manager:*"),
    COMMERCIAL_READ("commercial:read"),
    COMMERCIAL_UPDATE("commercial:update"),
    COMMERCIAL_DELETE("commercial:delete"),
    COMMERCIAL_CREATE("commercial:create"),
    COMMERCIAL_ALL("commercial:*"),
    PRODUCTION_READ("production:read"),
    PRODUCTION_UPDATE("production:update"),
    PRODUCTION_DELETE("production:delete"),
    PRODUCTION_CREATE("production:create"),
    PRODUCTION_ALL("production:*"),
    DISPATCH_READ("dispatch:read"),
    DISPATCH_UPDATE("dispatch:update"),
    DISPATCH_DELETE("dispatch:delete"),
    DISPATCH_CREATE("dispatch:create"),
    DISPATCH_ALL("dispatch:*"),
    BILLING_READ("billing:read"),
    BILLING_UPDATE("billing:update"),
    BILLING_DELETE("billing:delete"),
    BILLING_CREATE("billing:create"),
    BILLING_ALL("billing:*"),
    USER_READ("user:read"),
    ;

    @Getter
    private final String permission;
}
