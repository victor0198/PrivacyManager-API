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
public class MyKeys {
    @Id
//    @SequenceGenerator(
//            name = "key_sequence",
//            sequenceName = "key_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "_sequence"
//    )
    private long userId;
    private long keyId;
    private String filename;
    private String fileKey;
    private String fileChecksum;
}
