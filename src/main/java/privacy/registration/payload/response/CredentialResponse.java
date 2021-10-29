package privacy.registration.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialResponse {
    private long userId;
    private String service;
    private String login;
    private String password;
}
