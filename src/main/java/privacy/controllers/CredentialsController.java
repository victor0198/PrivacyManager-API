package privacy.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.CredentialsRepository;
import privacy.dao.OwnerRepository;
import privacy.models.MyCredentials;
import privacy.general.payload.request.CredentialRequest;
import privacy.registration.payload.response.MessageResponse;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CredentialsController {
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    /** Function to register new credential **/
    @PostMapping("/new_credential")

    public ResponseEntity<?> registerCredential(@RequestBody CredentialRequest credentialRequest) {
        if (credentialsRepository.findMyCredentialsByOwnerId(credentialRequest.getOwnerId()).contains(credentialRequest.getService())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Credential is already registered!"));
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();

        credentialRequest.setOwnerId(currentUserId);

        MyCredentials credential = new MyCredentials(credentialRequest.getOwnerId(),
                credentialRequest.getCredentialId(),
                credentialRequest.getService(),
                credentialRequest.getLogin(),
                credentialRequest.getPassword());

        System.out.println(credential);


        credentialsRepository.save(credential);
        return ResponseEntity.ok(credential);
//        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

    @GetMapping("/my_credentials")
    public ResponseEntity<List<MyCredentials>> getAllCredentials() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<MyCredentials> credentials = new ArrayList<MyCredentials>();
        try {
            credentials.addAll(credentialsRepository.findMyCredentialsByOwnerId(userId));

            if (credentials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/delete_credential/{credentialId}")
    public ResponseEntity<HttpStatus> deleteCredential(@PathVariable("credentialId") long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        try {
            credentialsRepository.deleteMyCredentialsByOwnerIdAndCredentialId(currentUserId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
