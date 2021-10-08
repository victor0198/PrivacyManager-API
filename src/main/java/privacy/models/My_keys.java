package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class My_keys {
    int userId;
    int keyId;
    String filename;
    String fileKey;
    String fileChecksum;
}
