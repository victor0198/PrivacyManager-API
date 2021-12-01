package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.general.payload.response.FriendshipResponse;
import privacy.general.payload.response.FriendshipResponseItem;
import privacy.models.Friendship;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.FriendshipResponseService;
import privacy.service.security.services.OwnerDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class FriendshipResponseController{
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final FriendshipResponseService friendshipResponseService;
    private final OwnerDetailsServiceImpl ownerDetailsService;


    /**
     * @param frResponse containing info about the current user's Id, the friendship's initiator Id, the symmetric key
     * being sent and the ACCEPT or REJECT status of the response
     * @return a ResponseEntity containing info about the newly registered friendship (from an object of type Friendship)
     * if the receiver accepts the friendship, or an object of type FriendshipRequestCreated with the status set to REJECT,
     * in case the receiver rejects the request
     */
    @PostMapping("/answer_new_fr_request")

    public ResponseEntity<?> registerResponse(@RequestBody FriendshipRequestAccepted frResponse) {
        if (friendshipResponseService.registerResponse(frResponse).getClass().getName().equals("Friendship")) {
            return ResponseEntity.ok(friendshipResponseService.registerResponse(frResponse));
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
        try {
            if (friendshipResponseService.getAllByRequests().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(friendshipResponseService.getAllByRequests(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve answered friendship requests for user "+ownerDetailsService.getUserIdFromToken());
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }

    @GetMapping("/responses_to_my_requests")
    public ResponseEntity<?> getResponsesToMyRequests() {
        try {
            return new ResponseEntity<>(friendshipResponseService.getResponsesToMyRequests(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't obtain responses to friendship requests for user "+ ownerDetailsService.getUserIdFromToken());
            return ( ResponseEntity.status(204).body("An unexpected error occurred"));
        }
    }
}

