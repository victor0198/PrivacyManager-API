package privacy.general.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOwnerResponse {
    private long ownerId;
    private String username;
}
