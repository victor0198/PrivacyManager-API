package privacy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import privacy.dao.CredentialsRepository;
import privacy.dao.OwnerRepository;
import privacy.models.ERole;
import privacy.models.MyCredentials;
import privacy.models.Owner;
import privacy.models.Role;
import privacy.registration.payload.request.CredentialRequest;
import privacy.registration.payload.response.CredentialResponse;
import privacy.registration.payload.response.JwtResponse;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.OwnerDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CredentialsController {
//    @Autowired
//    private CredentialRequest credentialRequest;
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    /** Function to register new credential **/
    @PostMapping("/new_credential")

    public ResponseEntity<?> registerCredential(@RequestBody CredentialRequest credentialRequest) {
        if (!credentialsRepository.findAllByUserId(credentialRequest.getUserId()).contains(credentialRequest.getService())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Credential is already registered!"));
        }


        MyCredentials credential = new MyCredentials(credentialRequest.getUserId(),
                credentialRequest.getCredentialId(),
                credentialRequest.getService(),
                credentialRequest.getLogin(),
                credentialRequest.getPassword());

        System.out.println(credential);


        credentialsRepository.save(credential);
        return ResponseEntity.ok(credential);
//        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

//    @GetMapping("/my_credentials/{username}")
//    public ResponseEntity<List<MyCredentials>> getAllCredentials() {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
//                .getPrincipal();
//        String username = userDetails.getUsername();
//        System.out.println("username "+ username);
//
//        Optional<Owner> userRecord = ownerRepository.findOwnerByUsername(username);
//        if (userRecord.isPresent()){
//            List<MyCredentials> credentials;
//            Long userId = userRecord.get().getOwnerId();
//            try {
//                credentials = new ArrayList<>(credentialsRepository.findAll());
//            } catch (Exception e) {
//                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//            if (credentials.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(credentials, HttpStatus.OK);
//
//        }else{
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//
//    }

    @GetMapping("/my_credentials")
    public ResponseEntity<List<MyCredentials>> getAllCredentials() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        try {
            List<MyCredentials> credentials = new ArrayList<MyCredentials>();


            credentials.addAll(credentialsRepository.findAllByUserId(userId));

            if (credentials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
