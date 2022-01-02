package privacy.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mail {
    private String recipient;
    private String subject;
    private String message;

    public Mail() {
    }

    public Mail(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }
}

