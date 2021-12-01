package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.dao.CredentialsRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.request.SearchAllOwnersRequest;
import privacy.general.payload.response.SearchOwnerResponse;
import privacy.models.Owner;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.OwnerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OwnersController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final OwnerService ownerService;

    /**
     * @return a list containing objects of type Owner to display all registered users
     */
    @GetMapping("/owners")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Owner> users = ownerService.getAllUsers();
            if (users.isEmpty()) {
                return ResponseEntity.status(204).body("No registered users");
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve information about all registered users");
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }

    /**
     *  This function returns a list of all possible matches for the username being searched.
     *      * It comes in handy when one doesn't know the entire username of the sought user.
     *      * In return to the search request, this function returns a list of usernames (only).
     *      * If the one searching found the username they needed, they may proceed to use the /searchOwners/{username}
     *      * endpoint to get a full description of the specified user and its username.
     *
     * @param ownerUsername - the searched user's possible username or a slice of it
     * @return a ResponseEntity containing info about the user being searched, when found
     */
    @GetMapping("/searchAllOwners/{username}")
    public ResponseEntity<?> getAllUsersByUsername(@PathVariable("username") String ownerUsername) {
        try {
            SearchOwnerResponse users = ownerService.getAllUsersByUsername(ownerUsername);
            if (users.getUsersFound().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't search all users matching the search' username");
            return ResponseEntity.status(204).body("An unexpected error occurred");
        }
    }


    /**
     * @param id of the owner being searched
     * @return a ResponseEntity containing an object of type Owner with info about the user being searched, when found
     */
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<Owner> getUsersById(@PathVariable("ownerId") long id) {
        return ownerService.getUsersById(id).map(owner -> new ResponseEntity<>(owner, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Search user by their username. In order to find a specific user, one must know the correct username.
     * @param ownerUsername - the username by which the search is performed
     * @return a single user and all the information about them.
     */
    @GetMapping("/searchOwners/{username}")
    public ResponseEntity<Owner> getUserByUsername(@PathVariable("username") String ownerUsername) {
        return ownerService.getUserByUsername(ownerUsername).map(owner -> new ResponseEntity<>(owner, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Request to clear the entire list of registered users
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/owners")
    public void deleteAllUsers() {
        ownerService.deleteAllUsers();
    }

    /**
     * Function to remove a user
     * @param id of the user
     * @return a ResponseEntity informing about the delete request status
     */
    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<?> deleteUser(@PathVariable("ownerId") long id) {
        try {
            ownerService.deleteUser(id);
            return ResponseEntity.status(200).body("Owner removed");
        } catch (Exception e) {
            logger.error("Couldn't delete user with id " + id);
            return ResponseEntity.status(417).body("An unexpected error occurred");
        }
    }
}
