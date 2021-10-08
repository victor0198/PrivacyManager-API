package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class My_credentials {
    int userId;
    int credentialId;
    String service;
    String login;
    String password;


}
