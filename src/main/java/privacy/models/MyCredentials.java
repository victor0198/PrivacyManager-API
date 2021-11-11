package privacy.models;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Getter
@Setter
//@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MyCredentials {

    @Id
    @SequenceGenerator(
            name = "credential_sequence",
            sequenceName = "credential_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "credential_sequence"
    )
    private Long recordId;
    private Long credentialId;
    private Long ownerId;
    private String service;
    private String login;
    private String password;

    public MyCredentials(Long userId, Long credentialId, String service, String login, String password) {
        this.ownerId = userId;
        this.credentialId = credentialId;
        this.service = service;
        this.login = login;
        this.password = password;
    }
}
