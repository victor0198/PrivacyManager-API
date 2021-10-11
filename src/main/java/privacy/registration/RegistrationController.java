package privacy.registration;

import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class RegistrationController {
    private RegistrationService registrationService;

    @PostMapping (path = "/registration")
    public JSONObject register(@RequestBody RegistrationRequest request) throws JSONException {
        return registrationService.register(request);
    }


}
