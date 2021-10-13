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
//@Data
public class Friendship {


    @SequenceGenerator(
            name = "friendship_sequence",
            sequenceName = "friendship_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "friendship_sequence"
    )
    @Id
    private long friendshipId;
    private long userOneId;
    private long userTwoId;

    public Friendship(long userOneId, long userTwoId) {
        this.userOneId = userOneId;
        this.userTwoId = userTwoId;
    }
}
