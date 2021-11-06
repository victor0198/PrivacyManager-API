package privacy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipResponsesRepository;
import privacy.dao.OwnerRepository;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.registration.payload.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class FriendshipResponseController{

    @Autowired
    private FriendshipResponsesRepository friendshipResponsesRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    /**
     * Function to register new friend request
     **/
    @PostMapping("/answer_new_fr_request")

    public ResponseEntity<?> registerResponse(@RequestBody FriendshipRequestAccepted frResponse) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();

        frResponse.setRequestAccepter(currentUserId);

        if (frResponse.getStatus() == "ACCEPT") {

            FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                    frResponse.getResponseToRequestId(),
                    frResponse.getRequestAccepter(),
                    frResponse.getFrInitiatorId(),
                    frResponse.getSymmetricKey(),
                    frResponse.getStatus());

            friendshipResponsesRepository.save(friendshipRequestResponse);
            return ResponseEntity.ok(friendshipRequestResponse);
        }else{
            FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                    frResponse.getResponseToRequestId(),
                    frResponse.getRequestAccepter(),
                    frResponse.getFrInitiatorId(),
                    frResponse.getSymmetricKey().replaceAll(frResponse.getSymmetricKey(), ""),
                    frResponse.getStatus());
            friendshipResponsesRepository.save(friendshipRequestResponse);
            return ResponseEntity.ok("Friendship rejected");
        }
//        return ResponseEntity.ok(new MessageResponse("Credential registered successfully!"));

    }

    @GetMapping("/received_fr_requests")
    public ResponseEntity<List<FriendshipRequestAccepted>> getAllByRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long accepterId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestAccepted> requestsList = new ArrayList<>();
        try {
            requestsList.addAll(friendshipResponsesRepository.getAllByRequestAccepter(currentUser));

            if (requestsList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(requestsList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

