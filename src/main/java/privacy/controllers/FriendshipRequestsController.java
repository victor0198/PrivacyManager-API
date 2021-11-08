package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipRequestsRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.response.FriendshipRequests;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.registration.payload.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;

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
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendshipRequestsController {

    private final FriendshipRequestsRepository friendshipRequestsRepository;

    private final OwnerRepository ownerRepository;


    /**
     * Function to register new friend request
     **/
    @PostMapping("/send_new_fr_request")

    public ResponseEntity<?> registerRequest(@RequestBody FriendshipRequestCreated frRequest) {
        if (friendshipRequestsRepository.findBySenderId(frRequest.getSenderId()).contains(frRequest.getReceiverId())) {
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

    @GetMapping("/sent_fr_requests")
    public ResponseEntity<List<FriendshipRequestCreated>> getAllSentFrRequests() {
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
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/received_fr_requests")
    public ResponseEntity<FriendshipRequests> getAllReceivedFrRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestCreated> requestsList = new ArrayList<>();
        requestsList.addAll(friendshipRequestsRepository.findFriendshipRequestCreatedByReceiverId(userId));

        FriendshipRequests frReq = new FriendshipRequests();
        frReq.setNotificationsList(requestsList);

        if (requestsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(frReq, HttpStatus.OK);
    }

}
