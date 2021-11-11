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
    @Id
    private Long keyId;
    private Long userId;
    @Column(columnDefinition="TEXT")
    private String fileKey;
    private String fileChecksum;
}
