package privacy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import privacy.dao.CredentialsRepository;
import privacy.models.ERole;
import privacy.models.MyCredentials;
import privacy.models.Owner;
import privacy.models.Role;
import privacy.registration.payload.request.CredentialRequest;
import privacy.registration.payload.response.CredentialResponse;
import privacy.registration.payload.response.JwtResponse;
import privacy.registration.payload.response.MessageResponse;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/auth/owners/{ownerId}")
public class CredentialsController {
    @Autowired
    private CredentialRequest credentialRequest;
    @Autowired
    private CredentialsRepository credentialsRepository;

    /** Function to register new credential **/
    @PostMapping("/new_credential")
    public ResponseEntity<?> registerCredential(@RequestBody CredentialRequest credentialRequest) {
        if (credentialsRepository.existsByService(credentialRequest.getService())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Credential is already registered!"));
        }


        MyCredentials credential = new MyCredentials(credentialRequest.getUserId(), credentialRequest.getService(), credentialRequest.getLogin(), credentialRequest.getPassword());

        System.out.println(credential);

        credentialsRepository.save(credential);
        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

    @GetMapping("/my_credentials")
    public ResponseEntity<List<MyCredentials>> getAllCredentials() {
        try {
            List<MyCredentials> credentials = new ArrayList<>(credentialsRepository.findAll());

            if (credentials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
