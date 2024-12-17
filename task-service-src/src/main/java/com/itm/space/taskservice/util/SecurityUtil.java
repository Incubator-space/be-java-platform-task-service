package com.itm.space.taskservice.util;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtil {

    public String getCurrentUserClaim(String claimName) {

        var securityContext = SecurityContextHolder.getContext();

        if (securityContext != null) {
            var authentication = securityContext.getAuthentication();
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                return jwt.getClaim(claimName);
            }
        }
        return null;
    }

    public String getCurrentUserPreferredName() {
        return getCurrentUserClaim("preferred_username");
    }

    @Nullable
    public UUID getCurrentUserId() {
        return UUID.fromString(getCurrentUserClaim("sub"));
    }

    public String getToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            return jwt.getTokenValue();
        }
        return null;
    }
}
