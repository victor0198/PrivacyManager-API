package privacy.old;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import privacy.models.Owner;

@Service
@AllArgsConstructor
public class SignUpService {
    private EmailValidator emailValidator;
    private final OwnerService ownerService;
//
//    public ResponseEntity<Owner> register(RegistrationRequest request) throws JSONException {
//        String email = request.getUsername().concat("@pm.com");
////        boolean isValidEmail = emailValidator.test(email);
////        if(!isValidEmail){
////            throw new IllegalStateException("email not valid");
////        }
//        return ownerService.signUpOwner(new Owner(request.getUsername(), email, request.getPassword()));
//    }

}
