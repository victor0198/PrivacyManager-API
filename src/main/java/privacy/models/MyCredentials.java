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

    @SequenceGenerator(
            name = "credential_sequence",
            sequenceName = "credential_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "credential_sequence"
    )
    @Id
    private long credentialId;
    private long userId;
    private String service;
    private String login;
    private String password;

    public MyCredentials(Long userId, String service, String login, String password) {
        this.userId = userId;
        this.service = service;
        this.login = login;
        this.password = password;
    }
}
