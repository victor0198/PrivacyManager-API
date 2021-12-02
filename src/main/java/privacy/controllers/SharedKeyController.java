package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.general.payload.request.ShareKeyRequest;
import privacy.general.payload.response.KeysForMeResponse;
import privacy.models.*;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.OwnerDetailsServiceImpl;
import privacy.service.security.services.SharedKeyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SharedKeyController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final SharedKeyService sharedKeyService;
    private final OwnerDetailsServiceImpl ownerDetailsService;

    @PostMapping("/share_new_key")
    public ResponseEntity<?> shareKey(@RequestBody ShareKeyRequest shareKeysRequest){
        sharedKeyService.shareKey(shareKeysRequest);
        return ResponseEntity.ok(new MessageResponse("The key is now shared"));
    }

    @GetMapping("/keys_for_me")
    public ResponseEntity<?> getKeysForMe() {
        try {
            KeysForMeResponse keysForMeResponse = sharedKeyService.getKeysForMe();
            if (keysForMeResponse.getKeysForMe().isEmpty()) {
                return new ResponseEntity<>(keysForMeResponse, HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(keysForMeResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing");
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }

    @GetMapping("/searchSharedKeys/{cloudKeyId}")
    public ResponseEntity<?> getSharedKeyByCloudKeyId(@PathVariable("cloudKeyId") Long cloudKeyId) {
        try {
            List<SharedKeys> userSharedKeys = sharedKeyService.getSharedKeyByCloudKeyId(cloudKeyId);
            if (userSharedKeys.isEmpty()) {
                return ResponseEntity.status(204).body("No registered cloud keys for sharing with this key id");
            }
            return new ResponseEntity<>(userSharedKeys, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing with this key id for user + " + ownerDetailsService.getUserIdFromToken());
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }

    }

    @GetMapping("/searchSharedKeysWithFriend/{user2Id}")
    public ResponseEntity<?> getSharedKeyByUser2Id(@PathVariable("user2Id") Long user2Id) {
        try {
            List<SharedKeys> userSharedKeys = sharedKeyService.getSharedKeyByUser2Id(user2Id);
            if (userSharedKeys.isEmpty()) {
                return ResponseEntity.status(204).body("No registered cloud keys for sharing with this key id");
            }
            return new ResponseEntity<>(userSharedKeys, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Couldn't retrieve information about all registered cloud keys for sharing with this user2 id for user + " + ownerDetailsService.getUserIdFromToken());
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }

    }


    /**
     * @return a ResponseEntity containing an object of type List with info about the user's friendships
     */
    @GetMapping("/searchSharedKeys/{cloudKeyId}/{friendshipId}")
    public ResponseEntity<?> getSharedKeyByCloudKeyIdOrFriendshipId(@PathVariable("cloudKeyId") Long cloudKeyId, @PathVariable("friendshipId") Long friendshipId) {
        try {
            List<SharedKeys> userSharedKeys = sharedKeyService.getSharedKeyByCloudKeyIdOrFriendshipId(cloudKeyId, friendshipId);
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
            sharedKeyService.deleteAllKeys();
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
    public ResponseEntity<?> deleteFriendship(@PathVariable("keyId") Long id) {
        try {
            sharedKeyService.deleteFriendship(id);
            return ResponseEntity.status(200).body("Key removed");
        } catch (Exception e) {
            logger.error("Couldn't delete key with id "+id);
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }
    }
}
