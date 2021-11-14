package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipRepository;
import privacy.dao.FriendshipRequestsRepository;
import privacy.dao.FriendshipResponsesRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.response.FriendshipResponse;
import privacy.general.payload.response.FriendshipResponseItem;
import privacy.models.Friendship;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;

import static privacy.models.new_friend.EStatus.ACCEPT;

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
 - of fr_request_accepted, containing the status - ACCEPT or REJECT */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendshipResponseController{
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final FriendshipResponsesRepository friendshipResponsesRepository;

    private final FriendshipRequestsRepository friendshipRequestsRepository;

    private final FriendshipRepository friendshipRepository;

    private final OwnerRepository ownerRepository;


    /**
     * @param frResponse containing info about the current user's Id, the friendship's initiator Id, the symmetric key
     * being sent and the ACCEPT or REJECT status of the response
     * @return a ResponseEntity containing info about the newly registered friendship (from an object of type Friendship)
     * if the receiver accepts the friendship, or an object of type FriendshipRequestCreated with the status set to REJECT,
     * in case the receiver rejects the request
     */
    @PostMapping("/answer_new_fr_request")

    public ResponseEntity<?> registerResponse(@RequestBody FriendshipRequestAccepted frResponse) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();

        frResponse.setRequestAccepter(currentUserId);

        if (frResponse.getStatus().name() == "ACCEPT") {

            FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                    frResponse.getResponseToRequestId(),
                    frResponse.getFrInitiatorId(),
                    frResponse.getRequestAccepter(),
                    frResponse.getSymmetricKey(),
                    frResponse.getStatus()); //get fr id

            friendshipResponsesRepository.save(friendshipRequestResponse);
            FriendshipRequestCreated answered = friendshipRequestsRepository.findFriendshipRequestCreatedBySenderIdAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter());
            answered.setStatus(ACCEPT);
            friendshipRequestsRepository.delete(answered);
            Friendship newFriendship = new Friendship(friendshipRequestResponse.getFrInitiatorId(), friendshipRequestResponse.getRequestAccepter());
            friendshipRepository.save(newFriendship);

            return ResponseEntity.ok(newFriendship);
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
            return ResponseEntity.ok(friendshipRequestResponse);
        }
    }

    /**
     * @return a list of FriendshipRequestAccepted pertaining to the current user
     */
    @GetMapping("/answered_fr_requests")
    public ResponseEntity<?> getAllByRequests() {
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
            logger.error("Couldn't retrieve answered friendship requests for user "+currentUser);
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }

    @GetMapping("/responses_to_my_requests")
    public ResponseEntity<?> getResponsesToMyRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long initiatorId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestAccepted> responsesList = new ArrayList<>();
        List<FriendshipResponseItem> responsesListItems = new ArrayList<>();
        try {
            responsesList.addAll(friendshipResponsesRepository.getAllByFrInitiatorId(initiatorId));

            for (FriendshipRequestAccepted item:
                    responsesList) {
                Friendship friendship = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(initiatorId, item.getRequestAccepter());
                if (friendship != null){
                    FriendshipResponseItem new_response = new FriendshipResponseItem(friendship, item);
                    responsesListItems.add(new_response);
                }

            }

            FriendshipResponse friendshipResponse = new FriendshipResponse();
            friendshipResponse.setResponses(responsesListItems);

            return new ResponseEntity<>(friendshipResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't obtain responses to friendship requests for user "+ currentUser);
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }
}

