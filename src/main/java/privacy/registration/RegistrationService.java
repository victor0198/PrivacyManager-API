package privacy.registration;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import privacy.models.Owner;
import privacy.service.OwnerService;

@Service
@AllArgsConstructor
public class RegistrationService {
    private EmailValidator emailValidator;
    private final OwnerService ownerService;

    public JSONObject register(RegistrationRequest request) throws JSONException {
        String email = request.getUsername().concat("@pm.com");
//        boolean isValidEmail = emailValidator.test(email);
//        if(!isValidEmail){
//            throw new IllegalStateException("email not valid");
//        }
        return ownerService.signUpOwner(new Owner(request.getUsername(), email, request.getPassword()));
    }

}
