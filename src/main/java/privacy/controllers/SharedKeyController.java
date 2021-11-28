package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.*;
import privacy.general.payload.request.ShareKeyRequest;
import privacy.general.payload.response.KeysForMeResponse;
import privacy.models.*;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.OwnerDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

//    @GetMapping("/sharedKeys")
//    public ResponseEntity<?> getAllSharedKeys() {
//        try {
//            List<SharedKeys> keys = new ArrayList<>(sharedKeyRepository.findAll());
//            KeysForMeResponse keysForMeResponse = new KeysForMeResponse();
//            keysForMeResponse.setKeysForMe(keys);
//
//            if (keys.isEmpty()) {
//                return new ResponseEntity<>(keysForMeResponse, HttpStatus.NO_CONTENT);
//            }
//
//            return new ResponseEntity<>(keysForMeResponse, HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("Couldn't retrieve information about all registered cloud keys for sharing");
//            return ResponseEntity.status(204).body("An unexpected error occurred");
//        }
//    }

    @GetMapping("/keys_for_me")
    public ResponseEntity<?> getKeysForMe() {
        try {
            logger.info("USER id:" + ownerDetailsService.getUserIdFromToken());
            List<Friendship> myFriendshipsTwo = friendshipRepository.findFriendshipsByUserTwoId(ownerDetailsService.getUserIdFromToken());
            List<Friendship> myFriendshipsOne = friendshipRepository.findFriendshipsByUserOneId(ownerDetailsService.getUserIdFromToken());
            logger.info("theFriendships:" + myFriendshipsOne.toString());
            logger.info("theFriendships:" + myFriendshipsTwo.toString());

            List<SharedKeys> allSharedKeys = new ArrayList<>();

            for (Friendship friendship:
                    myFriendshipsOne) {
                List<SharedKeys> sharedKeys = sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());
                allSharedKeys.addAll(sharedKeys);
            }

            for (Friendship friendship:
                    myFriendshipsTwo) {
                List<SharedKeys> sharedKeys = sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());
                allSharedKeys.addAll(sharedKeys);
            }

            logger.info("allSharedKeys:" + allSharedKeys.toString());

            List<CloudKeys> cloudKeys = new ArrayList<>();
            for (SharedKeys sharedKey:
                 allSharedKeys) {
                CloudKeys cloudKey = cloudKeysForSharingRepository.findCloudKeysByCloudKeyId(sharedKey.getCloudKeyId());
                cloudKeys.add(cloudKey);
            }

            logger.info("cloudKeys:" + cloudKeys.toString());


            KeysForMeResponse keysForMeResponse = new KeysForMeResponse();
            if (cloudKeys.isEmpty()) {
                cloudKeys.add(new CloudKeys(0,0,"",""));
            }
            keysForMeResponse.setKeysForMe(cloudKeys);

            if (cloudKeys.isEmpty()) {
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

    @GetMapping("/searchSharedKeysWithFriend/{user2Id}")
    public ResponseEntity<?> getSharedKeyByUser2Id(@PathVariable("user2Id") Long user2Id) {
        try {
            Optional<Friendship> try1 = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(ownerDetailsService.getUserIdFromToken(), user2Id);
            Optional<Friendship> try2 = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(user2Id,ownerDetailsService.getUserIdFromToken());
            Friendship friendship;
            if (try1.isPresent()){
                friendship = try1.get();
            } else if (try2.isPresent()){
                friendship = try1.get();
            } else{
                return ResponseEntity.badRequest().body(new MessageResponse("Error: You are not friends yet"));
            }

            List<SharedKeys> userSharedKeys = sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());

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
//            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName(); //will be needed for additional identity and possession checks
//            Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
            List<SharedKeys> userSharedKeys = sharedKeyRepository.findSharedKeysByCloudKeyIdEqualsAndFriendshipIdEquals(cloudKeyId, friendshipId);

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
