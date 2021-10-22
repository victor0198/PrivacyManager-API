package privacy.registration;

import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.models.Owner;

@RestController
@RequestMapping(value = "api/v1", method = RequestMethod.POST)
@AllArgsConstructor

public class RegistrationController {
    private SignUpService signUpService;

    @PostMapping (path = "/registration")
    public ResponseEntity<Owner> register(@RequestBody RegistrationRequest request) throws JSONException {
        return signUpService.register(request);
    }


}
