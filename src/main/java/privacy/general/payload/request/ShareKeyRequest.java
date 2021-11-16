package privacy.general.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShareKeyRequest {
    private Long friendshipId;
    private Long user2Id; //user gives this
    private Long keyId; //cloudKeyId
    private String fileKey;
    private String fileChecksum;
}
