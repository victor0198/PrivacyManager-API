package privacy.general.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialRequest {
    private Long ownerId;
    private Long CredentialId;
    private String service;
    private String login;
    private String password;
}
