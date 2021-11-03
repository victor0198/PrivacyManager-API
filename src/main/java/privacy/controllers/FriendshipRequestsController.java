package privacy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipRequestsRepository;
import privacy.dao.OwnerRepository;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.registration.payload.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class FriendshipRequestsController {
    @Autowired
    private FriendshipRequestsRepository friendshipRequestsRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    /** Function to register new credential **/
    @PostMapping("/new_fr_request")

    public ResponseEntity<?> registerRequest(@RequestBody FriendshipRequestCreated frRequest) {
        if (friendshipRequestsRepository.findBySenderId(frRequest.getSenderId()).contains(frRequest.getReceiverId())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Request is already sent!"));
        }
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();

        frRequest.setSenderId(userId);

        FriendshipRequestCreated friendshipRequestCreated = new FriendshipRequestCreated(frRequest.getCreatedRequestId(),
                frRequest.getSenderId(),
                frRequest.getReceiverId(),
                frRequest.getPublicKey());

        friendshipRequestsRepository.save(friendshipRequestCreated);
        return ResponseEntity.ok(friendshipRequestCreated);
//        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

    @GetMapping("/received_fr_requests")
    public ResponseEntity<List<FriendshipRequestCreated>> getAllFrRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestCreated> requestsList = new ArrayList<>();
        try {
            requestsList.addAll(friendshipRequestsRepository.findBySenderId(userId));

            if (requestsList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(requestsList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/received_fr_requests/{requestId}")
    public ResponseEntity<HttpStatus> deleteCredential(@PathVariable("requestId") long id) {
        try {
            friendshipRequestsRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
