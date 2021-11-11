package privacy.models.new_friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static privacy.models.new_friend.EStatus.PENDING;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequestCreated {
    public FriendshipRequestCreated(long createdRequestId, long senderId, String senderUsername, long receiverId, String publicKey) {
        this.createdRequestId = createdRequestId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.receiverId = receiverId;
        this.publicKey = publicKey;
    }

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
    private String senderUsername;
    private long receiverId;
    @Column(columnDefinition="TEXT")
    private String publicKey;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status = PENDING;

}
