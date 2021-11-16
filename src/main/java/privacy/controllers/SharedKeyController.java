package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.*;
import privacy.general.payload.request.CredentialRequest;
import privacy.general.payload.request.ShareKeyRequest;
import privacy.models.CloudKeys;
import privacy.models.MyCredentials;
import privacy.models.MyKeys;
import privacy.models.SharedKeys;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.OwnerDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SharedKeyController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final OwnerRepository ownerRepository;
    private final OwnerDetailsServiceImpl ownerDetailsService;
    private final CloudKeysForSharingRepository cloudKeysForSharingRepository;
    private final KeyRepository keyRepository; //cloud keys
    private final SharedKeyRepository sharedKeyRepository;
    private final FriendshipRepository friendshipRepository; //will be needed later on

    @PostMapping("/share_new_key")
    public ResponseEntity<?> shareKey(@RequestBody ShareKeyRequest shareKeysRequest){
//        if (sharedKeyRepository.findSharedKeysByFriendshipId(shareKeysRequest.getFriendshipId()).isPresent()){
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: This key is already registered!"));
//        }
//        MyKeys sharingKey = new MyKeys(ownerDetailsService.getUserIdFromToken(), shareKeysRequest.getFileKey(), shareKeysRequest.getFileChecksum());
        CloudKeys cloudKey = new CloudKeys(ownerDetailsService.getUserIdFromToken(), shareKeysRequest.getKeyId(), shareKeysRequest.getFileKey(), shareKeysRequest.getFileChecksum());
        cloudKeysForSharingRepository.save(cloudKey);
        SharedKeys newlyCreatedCloudKey = new SharedKeys(cloudKey.getCloudKeyId(), shareKeysRequest.getFriendshipId());
        sharedKeyRepository.save(newlyCreatedCloudKey);
        return ResponseEntity.ok(new MessageResponse("The key is now shared"));

    }

    @GetMapping("/sharedKeys")
    public ResponseEntity<?> getAllSharedKeys() {
        try {
            List<SharedKeys> keys = new ArrayList<>(sharedKeyRepository.findAll());

            if (keys.isEmpty()) {
                return ResponseEntity.status(204).body("No registered keys");
            }
            return new ResponseEntity<>(keys, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing");
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }

    @GetMapping("/searchSharedKeys/{cloudKeyId}")
    public ResponseEntity<?> getSharedKeyByCloudKeyId(@PathVariable("cloudKeyId") Long cloudKeyId) {
        try {
            List<SharedKeys> userSharedKeys = sharedKeyRepository.findSharedKeysByCloudKeyId(cloudKeyId);

            if (userSharedKeys.isEmpty()) {
                return ResponseEntity.status(204).body("No registered cloud keys for sharing with this key id");
            }
            return new ResponseEntity<>(userSharedKeys, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing with this key id for user + " + ownerDetailsService.getUserIdFromToken());
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }

    }


    /**
     * @return a ResponseEntity containing an object of type List with info about the user's friendships
     */
    @GetMapping("/searchSharedKeys/{cloudKeyId}/{friendshipId}")
    public ResponseEntity<?> getSharedKeyByCloudKeyIdOrFriendshipId(@PathVariable("cloudKeyId") Long cloudKeyId, @PathVariable("friendshipId") Long friendshipId) {
        try {
//            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName(); //will be needed for additional identity and possession checks
//            Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
            List<SharedKeys> userSharedKeys = sharedKeyRepository.findSharedKeysByCloudKeyIdEqualsOrFriendshipIdEquals(cloudKeyId, friendshipId);

            if (userSharedKeys.isEmpty()) {
                return ResponseEntity.status(204).body("No registered cloud keys for sharing with this key id or this friendship id");
            }
            return new ResponseEntity<>(userSharedKeys, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing with this key or friendship id for user + " + ownerDetailsService.getUserIdFromToken());
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }

    }


    /**
     * Request to clear the entire list of registered friendships
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/sharedKeys")
    public ResponseEntity<?> deleteAllKeys() {
        try {
            sharedKeyRepository.deleteAll();
            return ResponseEntity.status(200).body("Keys deleted");
        } catch (Exception e) {
            logger.error("Couldn't process request to delete all shared keys from database");
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }

    }

    /**
     * Function to remove a shared key that belongs to a specific user
     * @param id of the user
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/sharedKeys/{keyId}")
    public ResponseEntity<?> deleteFriendship(@PathVariable("keyId") long id) {
        try {
            sharedKeyRepository.deleteById(id);
            return ResponseEntity.status(200).body("Key removed");
        } catch (Exception e) {
            logger.error("Couldn't delete key with id "+id);
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }
    }
}
