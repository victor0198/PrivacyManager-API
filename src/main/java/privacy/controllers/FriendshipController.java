package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.FriendshipRepository;
import privacy.dao.OwnerRepository;
import privacy.models.Friendship;
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendshipController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final OwnerRepository ownerRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * @return a list containing objects of type Friendship to display all registered friendships
     */
    @GetMapping("/friendships")
    public ResponseEntity<?> getAllFriendships() {
        try {
            List<Friendship> friendships = new ArrayList<>(friendshipRepository.findAll());

            if (friendships.isEmpty()) {
                return ResponseEntity.status(204).body("No registered friendships");
            }
            return new ResponseEntity<>(friendships, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve information about all registered friendships");
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }


    /**
     * @return a ResponseEntity containing an object of type List with info about the user's friendships
     */
    @GetMapping("/searchUserFriendships")
    public ResponseEntity<?> getFriendshipById() {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
            List<Friendship> userFriendships = friendshipRepository.findFriendshipsByUserOneIdEqualsOrUserTwoIdEquals(userId, userId);

            if (userFriendships.isEmpty()) {
                return ResponseEntity.status(204).body("No registered friendships");
            }
            return new ResponseEntity<>(userFriendships, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Couldn't retrieve information about all registered friendships for user + " );
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }

    }


    /**
     * Request to clear the entire list of registered friendships
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/friendships")
    public ResponseEntity<?> deleteAllFriendships() {
        try {
            friendshipRepository.deleteAll();
            return ResponseEntity.status(200).body("Friendships deleted");
        } catch (Exception e) {
            logger.error("Couldn't process request to delete all friendships from database");
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }

    }

    /**
     * Function to remove a friendship that belongs to a specific user
     * @param id of the user
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/friendships/{friendshipId}")
    public ResponseEntity<?> deleteFriendship(@PathVariable("friendshipId") long id) {
        try {
            friendshipRepository.deleteById(id);
            return ResponseEntity.status(200).body("Friendship removed");
        } catch (Exception e) {
            logger.error("Couldn't delete friendship with id "+id);
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }
    }

}
