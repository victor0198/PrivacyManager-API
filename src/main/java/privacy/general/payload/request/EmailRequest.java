package privacy.general.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailRequest {
    String subject;
    String toAddresses;
    String ccAddresses;
    String bccAddresses;
    String body;
}
