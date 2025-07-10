package ch.janishuber.adapter.socket;

import ch.janishuber.adapter.helpers.AuthHelper;
import ch.janishuber.adapter.persistence.MessageRepository;
import ch.janishuber.domain.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SocketService {

    @Inject
    private AuthHelper authHelper;
    @Inject
    private MessageRepository messageRepository;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public String extractToken(String queryString) {
        if (queryString == null || !queryString.contains("token=")) {
            throw new IllegalArgumentException("Missing token in query string");
        }
        String[] params = queryString.split("&");
        for (String param : params) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        throw new IllegalArgumentException("Token not found in query string");
    }

    public String verifyAndExtractUid(String token) {
        return authHelper.verifyAndExtractUid(token);
    }
}
