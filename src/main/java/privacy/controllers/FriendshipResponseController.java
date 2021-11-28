package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import privacy.models.Owner;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.models.new_friend.FriendshipRequestCreated;
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static privacy.models.new_friend.EStatus.ACCEPT;

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
        frResponse.setAccepterUsername(currentUser);
        Optional<Owner> initiatorUser = ownerRepository.findById(frResponse.getFrInitiatorId());
        if (initiatorUser.isPresent() && friendshipRequestsRepository.existsFriendshipRequestCreatedBySenderIdAndAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter())) {
            frResponse.setInitiatorUsername(initiatorUser.get().getUsername());

            if (frResponse.getStatus().name() == "ACCEPT") {

                FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                        frResponse.getResponseToRequestId(),
                        frResponse.getFrInitiatorId(),
                        frResponse.getInitiatorUsername(),
                        frResponse.getRequestAccepter(),
                        frResponse.getAccepterUsername(),
                        frResponse.getSymmetricKey(),
                        frResponse.getStatus());

                friendshipResponsesRepository.save(friendshipRequestResponse);
                FriendshipRequestCreated answered = friendshipRequestsRepository.findFriendshipRequestCreatedBySenderIdAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter());
                answered.setStatus(ACCEPT);
                friendshipRequestsRepository.delete(answered);
                Friendship newFriendship = new Friendship(friendshipRequestResponse.getFrInitiatorId(), friendshipRequestResponse.getRequestAccepter());
                friendshipRepository.save(newFriendship);

                return ResponseEntity.ok(newFriendship);
            } else {
                FriendshipRequestAccepted friendshipRequestResponse = new FriendshipRequestAccepted(
                        frResponse.getResponseToRequestId(),
                        frResponse.getFrInitiatorId(),
                        frResponse.getInitiatorUsername(),
                        frResponse.getRequestAccepter(),
                        frResponse.getAccepterUsername(),
                        frResponse.getSymmetricKey().replaceAll(frResponse.getSymmetricKey(), ""),
                        frResponse.getStatus());
                friendshipResponsesRepository.save(friendshipRequestResponse);
                FriendshipRequestCreated answered = friendshipRequestsRepository.findFriendshipRequestCreatedBySenderIdAndReceiverId(frResponse.getFrInitiatorId(), frResponse.getRequestAccepter());
                friendshipRequestsRepository.delete(answered);
                return ResponseEntity.ok(friendshipRequestResponse);
            }
        }
        else {
            return ResponseEntity.status(404).body("Error: friendship initiator with this id found");
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
                Optional<Friendship> friendshipTry = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(initiatorId, item.getRequestAccepter());
                if (friendshipTry.isPresent()){
                    Friendship friendship = friendshipTry.get();
                    item.setAccepterUsername(ownerRepository.findById(item.getRequestAccepter()).get().getUsername());
                    FriendshipResponseItem new_response = new FriendshipResponseItem(friendship, item);
                    responsesListItems.add(new_response);
                }

            }

            FriendshipResponse friendshipResponse = new FriendshipResponse();
            friendshipResponse.setResponses(responsesListItems);

            return new ResponseEntity<>(friendshipResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't obtain responses to friendship requests for user "+ currentUser);
            return ( ResponseEntity.status(204).body("An unexpected error occurred"));
        }
    }
}

