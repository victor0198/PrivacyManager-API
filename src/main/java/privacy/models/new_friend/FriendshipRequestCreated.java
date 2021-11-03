package privacy.models.new_friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequestCreated {
    @Id
    @SequenceGenerator(
            name = "created_request_sequence",
            sequenceName = "created_request_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "created_request_sequence"
    )
    private long createdRequestId;
    private long senderId;
    private long receiverId;
    private String publicKey;
}
