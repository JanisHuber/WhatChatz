package ch.janishuber.application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.io.InputStream;
import java.util.logging.Logger;

@Singleton
@Startup
public class FirebaseInitializer {

    private static final Logger logger = Logger.getLogger(FirebaseInitializer.class.getName());

    @PostConstruct
    public void init() {
        logger.info("🔥 Firebase Initializer gestartet");
        try {
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("whatchatz-firebase-adminsdk-fbsvc-8b5abfc2b9.json");
            if (serviceAccount == null) {
                throw new IllegalStateException("⚠️ Firebase-Credentials nicht gefunden! Pfad im Classpath prüfen.");
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
