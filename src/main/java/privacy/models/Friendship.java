package privacy.models;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Long friendshipId;
    private Long userOneId;
    private Long userTwoId;

    public Friendship(Long requestAccepter, Long frInitiatorId) {
        this.userOneId = requestAccepter;
        this.userTwoId = frInitiatorId;
    }
}
