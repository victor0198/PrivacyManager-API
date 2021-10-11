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
public class Friendship {
    @Id
    @SequenceGenerator(
            name = "friendship_sequence",
            sequenceName = "friendship_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "friendship_sequence"
    )
    private long friendshipId;
    private int userOneId;
    private int userTwoId;
}
