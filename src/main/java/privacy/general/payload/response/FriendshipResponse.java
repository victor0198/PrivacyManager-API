package privacy.general.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipResponse {
    private long responseToRequestId;
    private long frInitiatorId;
    private long requestAccepter;
    private String symmetricKey;
    private String status;
}
