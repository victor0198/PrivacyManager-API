package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyCredentials {
    private int userId;
    private int credentialId;
    private String service;
    private String login;
    private String password;


}
