package privacy.service.security.jwt.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long ownerId;
    private String username;
    private String email;
    private List<String> roles;

    public void setType(){
        this.type = "Bearer";
    }
}
