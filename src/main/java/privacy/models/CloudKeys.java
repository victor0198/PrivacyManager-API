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
public class CloudKeys {
    @Id
    @SequenceGenerator(
            name = "key_sequence",
            sequenceName = "key_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "key_sequence"
    )
    private long cloudKeyId;
    private int ownerId;
    private int keyId;
    private String fileKey;
    private String fileChecksum;
}
