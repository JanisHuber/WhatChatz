package ch.janishuber.adapter.socket;

import ch.janishuber.adapter.helpers.AuthHelper;
import ch.janishuber.adapter.persistence.ContactRepository;
import ch.janishuber.adapter.persistence.MessageRepository;
import ch.janishuber.domain.Contact;
import ch.janishuber.domain.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SocketService {

    @Inject
    private AuthHelper authHelper;
    @Inject
    private MessageRepository messageRepository;
    @Inject
    private ContactRepository contactRepository;

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

    public String generateChatId(String uid1, String uid2) {
        if (uid1.compareTo(uid2) < 0) {
            return uid1 + "__" + uid2;
        } else {
            return uid2 + "__" + uid1;
        }
    }

    public List<String> getAllChatIdsFor(String uid) {
        List<String> chatIds = new ArrayList<>();
        List<Contact> contacts = this.contactRepository.getAllContactsFor(uid);

        for (Contact contact : contacts) {
            String chatId = generateChatId(uid, contact.contactId());
            chatIds.add(chatId);
        }
        return chatIds;
    }
}
