package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cloud_keys {
    int cloud_keyId;
    int ownerId;
    int keyId;
    String fileKey;
    String fileChecksum;
}
