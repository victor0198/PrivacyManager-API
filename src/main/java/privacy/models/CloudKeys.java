package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CloudKeys {
    private int cloudKeyId;
    private int ownerId;
    private int keyId;
    private String fileKey;
    private String fileChecksum;
}
