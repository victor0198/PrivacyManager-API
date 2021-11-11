package privacy.models;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Getter
@Setter
//@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MyKeys {
//    @SequenceGenerator(
//            name = "key_sequence",
//            sequenceName = "key_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "_sequence"
//    )
//    private long id;
    @Id
    private long keyId;
    private String filename;
    @Column(length=2000)
    private String fileKey;
    private String fileChecksum;

}
