package privacy.general.payload.response;

import lombok.Getter;
import lombok.Setter;
import privacy.models.Friendship;
import privacy.models.new_friend.EStatus;
import privacy.models.new_friend.FriendshipRequestAccepted;

@Getter
@Setter
public class FriendshipResponseItem {
    private long friendshipId;
    private long responseToRequestId;
    private long frInitiatorId;
    private long requestAccepter;
    private String accepterUsername;
    private String symmetricKey;
    private EStatus status;




    public FriendshipResponseItem(Friendship friendship, FriendshipRequestAccepted friendshipRequestAccepted) {
        this.friendshipId = friendship.getFriendshipId();
        this.responseToRequestId = friendshipRequestAccepted.getResponseToRequestId();
        this.frInitiatorId = friendshipRequestAccepted.getFrInitiatorId();
        this.requestAccepter = friendshipRequestAccepted.getRequestAccepter();
        this.accepterUsername = friendshipRequestAccepted.getAccepterUsername();
        this.symmetricKey = friendshipRequestAccepted.getSymmetricKey();
        this.status = friendshipRequestAccepted.getStatus();
    }
}
