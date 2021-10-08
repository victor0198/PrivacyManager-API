package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyKeys {
    private int userId;
    private int keyId;
    private String filename;
    private String fileKey;
    private String fileChecksum;
}
