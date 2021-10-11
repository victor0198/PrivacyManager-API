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
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("email not valid");
        }

        return ownerService.signUpOwner(new Owner(request.getFirstName(), request.getEmail(), request.getPassword()));
    }

}
