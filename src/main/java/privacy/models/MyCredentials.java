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
    private long recordId;
    private long credentialId;
    private long userId;
    private String service;
    private String login;
    private String password;

    public MyCredentials(Long userId, Long credentialId, String service, String login, String password) {
        this.userId = userId;
        this.credentialId = credentialId;
        this.service = service;
        this.login = login;
        this.password = password;
    }
}
