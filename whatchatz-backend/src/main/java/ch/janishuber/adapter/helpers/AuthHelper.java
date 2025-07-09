package ch.janishuber.adapter.helpers;

import ch.janishuber.domain.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

public class AuthHelper {
    @Inject
    private AuthService authService;


    public String verifyAndExtractUid(HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Unauthorized: Missing or invalid Authorization header");
        }
        String idToken = authHeader.substring("Bearer ".length());

        return verifyAndExtractUid(idToken);
    }

    public String verifyAndExtractUid(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Unauthorized: Missing or invalid token");
        }

        String uid;
        try {
            uid = authService.extractUidFromToken(token);
        } catch (Exception e) {
            throw new IllegalStateException("Unauthorized: Invalid token");
        }

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException("Unauthorized: Could not extract UID");
        }

        return uid;
    }
}
