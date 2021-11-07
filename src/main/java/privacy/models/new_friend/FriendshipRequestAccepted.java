package privacy.models.new_friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import privacy.models.ERole;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendshipRequestAccepted {
    @Id
    @SequenceGenerator(
            name = "request_response_sequence",
            sequenceName = "request_response_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_response_sequence"
    )
    private long responseToRequestId;
    private long frInitiatorId;
    private long requestAccepter;
    private String symmetricKey;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status;
}
