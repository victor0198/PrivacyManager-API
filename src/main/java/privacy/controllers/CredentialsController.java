package privacy.controllers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CredentialsController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final CredentialsRepository credentialsRepository;

    private final OwnerRepository ownerRepository;


    /**
     *  Function to register new credential
     * @param credentialRequest - request to register new credentials
     * @return credential object
     */
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

        credentialsRepository.save(credential);
        logger.info("Saved credential: "+credential+" for ");
        return ResponseEntity.ok(credential);
//        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

    /**
     * Function to display the current user's credentials
     * @return a ResponseEntity containing info about the credentials
     */
    @GetMapping("/my_credentials")
    public ResponseEntity<?> getAllCredentials() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<MyCredentials> credentials = new ArrayList<MyCredentials>();
        try {
            credentials.addAll(credentialsRepository.findMyCredentialsByOwnerId(userId));

            if (credentials.isEmpty()) {
                return ResponseEntity.status(204).body("No registered credentials");
            }

            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't find resource for user "+userId);
            return ResponseEntity.status(404).body("Couldn't find resource");
        }
    }

    /**
     * @param id of the credential that is to be removed
     * @return a ResponseEntity confirming the removal or informing about http error 417
     */
    @DeleteMapping("/delete_credential/{credentialId}")
    public ResponseEntity<?> deleteCredential(@PathVariable("credentialId") long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        try {
            credentialsRepository.deleteMyCredentialsByOwnerIdAndCredentialId(currentUserId, id);
            return ResponseEntity.ok("Credential removed");
        } catch (Exception e) {
            logger.error("Couldn't remove credential for user "+currentUserId);
            return ResponseEntity.status(417).body("Couldn't process request");
        }
    }
}
