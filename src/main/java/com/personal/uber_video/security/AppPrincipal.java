package com.personal.uber_video.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * Custom UserDetails implementation that carries entity ID and type information.
 * This allows controllers to distinguish between User and Captain principals
 * and fetch the correct entity from the appropriate repository.
 */
public class AppPrincipal extends org.springframework.security.core.userdetails.User {

    public enum EntityType { USER, CAPTAIN }

    private final UUID entityId;
    private final EntityType entityType;

    public AppPrincipal(String username, String password,
                        Collection<? extends GrantedAuthority> authorities,
                        UUID entityId, EntityType entityType) {
        super(username, password, authorities);
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
