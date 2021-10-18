package privacy.registration;

import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1", method = RequestMethod.POST)
@AllArgsConstructor

public class RegistrationController {
    private RegistrationService registrationService;

    @PostMapping (path = "/registration")
    public JSONObject register(@RequestBody RegistrationRequest request) throws JSONException {
        return registrationService.register(request);
    }


}
