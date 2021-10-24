package privacy.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import privacy.old.SignUpService;


/** This controller will eventually be safely deleted **/

@RestController
@RequestMapping(value = "api/v1", method = RequestMethod.POST)
@AllArgsConstructor

public class RegistrationController {
    private SignUpService signUpService;

//    @PostMapping (path = "/registration")
//    public ResponseEntity<Owner> register(@RequestBody RegistrationRequest request) throws JSONException {
//        return signUpService.register(request);
//    }


}
