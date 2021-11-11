package privacy.models;
import jdk.jfr.Timestamp;
import lombok.*;

import javax.persistence.*;
@Table
@Entity
@ToString
@Getter
@Setter
//@EqualsAndHashCode
@NoArgsConstructor
public class CloudKeys {
    @Id
    @SequenceGenerator(
            name = "cl_key_sequence",
            sequenceName = "cl_key_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cl_key_sequence"
    )

    private long cloudKeyId;
    private long ownerId;
    private long keyId;
    private String fileKey;
    private String fileChecksum;

    public CloudKeys(long ownerId, long keyId, String fileKey, String fileChecksum) {
        this.ownerId = ownerId;
        this.keyId = keyId;
        this.fileKey = fileKey;
        this.fileChecksum = fileChecksum;
    }

}
