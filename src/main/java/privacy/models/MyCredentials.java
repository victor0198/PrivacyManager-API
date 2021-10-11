package privacy.models;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private long userId;
    @Id
    private long credentialId;
    private String service;
    private String login;
    private String password;


}
