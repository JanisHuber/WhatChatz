package ch.janishuber.application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.io.FileInputStream;
import java.io.InputStream;

@Singleton
@Startup
public class FirebaseInitializer {

    @PostConstruct
    public void init() {
        try {
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (credentialsPath == null || credentialsPath.isEmpty()) {
                throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS ist nicht gesetzt");
            }
            InputStream serviceAccount = new FileInputStream(credentialsPath);

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
