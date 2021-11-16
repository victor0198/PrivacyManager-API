package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendshipRequestsController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final FriendshipRequestsRepository friendshipRequestsRepository;

    private final OwnerRepository ownerRepository;


    /**
     * @param frRequest - contains the senderID - automatically retrieved from JWT, the receiverID and the publicKey
     * @return a ResponseEntity containing info about an object of type FriendshipRequestCreated with
     * the senderID, the senderUsername, the receiverId and the publicKey
     */
    @PostMapping("/send_new_fr_request")

    public ResponseEntity<?> registerRequest(@RequestBody FriendshipRequestCreated frRequest) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        frRequest.setSenderId(userId);
        frRequest.setSenderUsername(currentUser);

        boolean alreadySent = friendshipRequestsRepository.existsFriendshipRequestCreatedBySenderIdAndAndReceiverId(frRequest.getSenderId(), frRequest.getReceiverId());
        if (alreadySent) {
            logger.warn("Sending friendship request multiple times: user "+frRequest.getSenderId()+" to user "+frRequest.getReceiverId());
            return new ResponseEntity<>(new FriendshipRequestCreated(), HttpStatus.NOT_ACCEPTABLE);
        } else if (friendshipRequestsRepository.existsFriendshipRequestCreatedBySenderIdAndAndReceiverId(frRequest.getReceiverId(), frRequest.getSenderId())) {
            logger.warn("The other user has already sent a request to user "+frRequest.getSenderId());
            return new ResponseEntity<>(new FriendshipRequestCreated(), HttpStatus.CONFLICT);
        } else if (frRequest.getSenderId() == frRequest.getReceiverId()){
            logger.warn("Trying to send friendship request to oneself: user "+frRequest.getSenderId());
            return new ResponseEntity<>(new FriendshipRequestCreated(), HttpStatus.METHOD_NOT_ALLOWED);
        }else {

            FriendshipRequestCreated friendshipRequestCreated = new FriendshipRequestCreated(frRequest.getCreatedRequestId(),
                    frRequest.getSenderId(),
                    frRequest.getSenderUsername(),
                    frRequest.getReceiverId(),
                    frRequest.getPublicKey());

            friendshipRequestsRepository.save(friendshipRequestCreated);
            return ResponseEntity.ok(friendshipRequestCreated);
        }

    }

    /**
     * @return a ResponseEntity with a list of objects of type FriendshipRequestCreated
     * displaying the friendship requests sent by the current user
     */
    @GetMapping("/sent_fr_requests")
    public ResponseEntity<?> getAllSentFrRequests() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        List<FriendshipRequestCreated> requestsList = new ArrayList<>();
        try {
            requestsList.addAll(friendshipRequestsRepository.findBySenderId(userId));

            if (requestsList.isEmpty()) {
                return ResponseEntity.status(204).body("You have no pending requests");
            }

            return new ResponseEntity<>(requestsList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("No content was found for friendship requests sent by user "+userId);
            return ResponseEntity.badRequest().body("An unexpected error occurred");
        }
    }

    /**
     * @return a ResponseEntity with a list FriendshipRequests containing objects of type FriendshipRequestCreated
     * displaying info about friendship requests received by the current user
     */
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
