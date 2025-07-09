package ch.janishuber.domain;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class AuthService {

    public String extractUidFromToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            System.out.println("✅ [AuthService] UID extracted: " + decodedToken.getUid());
            return decodedToken.getUid();
        } catch (Exception e) {
            System.err.println("🚫 [AuthService] Invalid token: " + e.getMessage());
            throw new RuntimeException("Invalid Firebase token", e);
        }
    }
}
