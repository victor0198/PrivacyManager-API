package privacy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipRequestsRepository;
import privacy.dao.FriendshipResponsesRepository;
import privacy.dao.OwnerRepository;
import privacy.models.new_friend.EStatus;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.registration.payload.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static privacy.models.new_friend.EStatus.ACCEPT;
import static privacy.models.new_friend.EStatus.REJECT;

/** Friendship requests and answers:

 Send a request as user 1 (for example), on localhost:8080/api/send_new_fr_request:
 {
 "receiverId": 4,
 "publicKey": "publicKeyFrom6to8"
 }
 As user 4:
 Check your requests on localhost:8080/api/received_fr_requests; //the status of the request is set to PENDING in the requests table
 Answer a request on localhost:8080/api/answer_new_fr_request:
 {
 "frInitiatorId":1,
 "symmetricKey":"symmetricKey4For1",
 "status":"ACCEPT" //or REJECT, in which case - no symmetric key will be saved
 }
 The sent request gets deleted from the firs table - fr_request_created - and the answer is saved in the second table
 - of fr_request_accepted, containing the status - ACCEPT or REJECT **/
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class FriendshipResponseController{

    @Autowired
    private FriendshipResponsesRepository friendshipResponsesRepository;

    @Autowired
    private FriendshipRequestsRepository friendshipRequestsRepository;
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
        System.out.println(frResponse.getRequestAccepter());

        if (frResponse.getStatus().name() == "ACCEPT") {

            FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                    frResponse.getResponseToRequestId(),
                    frResponse.getFrInitiatorId(),
                    frResponse.getRequestAccepter(),
                    frResponse.getSymmetricKey(),
                    frResponse.getStatus());

            friendshipResponsesRepository.save(friendshipRequestResponse);
            FriendshipRequestCreated answered = friendshipRequestsRepository.findFriendshipRequestCreatedBySenderIdAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter());
            answered.setStatus(ACCEPT);
            friendshipRequestsRepository.save(answered);
            return ResponseEntity.ok(friendshipRequestResponse);
        }else{
            FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                    frResponse.getResponseToRequestId(),
                    frResponse.getRequestAccepter(),
                    frResponse.getFrInitiatorId(),
                    frResponse.getSymmetricKey().replaceAll(frResponse.getSymmetricKey(), ""),
                    frResponse.getStatus());
            friendshipResponsesRepository.save(friendshipRequestResponse);
            FriendshipRequestCreated answered = friendshipRequestsRepository.findFriendshipRequestCreatedBySenderIdAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter());
            friendshipRequestsRepository.delete(answered);
            return ResponseEntity.ok("Friendship rejected");
        }
    }

    @GetMapping("/answered_fr_requests")
    public ResponseEntity<List<FriendshipRequestAccepted>> getAllByRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long accepterId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestAccepted> requestsList = new ArrayList<>();
        try {
            requestsList.addAll(friendshipResponsesRepository.getAllByRequestAccepter(accepterId));

            if (requestsList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(requestsList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }
}

