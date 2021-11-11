package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.dao.CredentialsRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.request.SearchAllOwnersRequest;
import privacy.general.payload.response.SearchOwnerResponse;
import privacy.models.Owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OwnersController {

    private final CredentialsRepository credentialsRepository;

    private final OwnerRepository ownerRepository;

    /** Get information about all users **/
    @GetMapping("/owners")
    public ResponseEntity<List<Owner>> getAllUsers() {
        try {
            List<Owner> users = new ArrayList<>(ownerRepository.findAll());

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    /**
     *  This function returns a list of all possible matches for the username being searched.
     *      * It comes in handy when one doesn't know the entire username of the sought user.
     *      * In return to the search request, this function returns a list of usernames (only).
     *      * If the one searching found the username they needed, they may proceed to use the /searchOwners/{username}
     *      * endpoint to get a full description of the specified user and its username.
     * @param ownerUsername
     * @return
     */
    @GetMapping("/searchAllOwners/{username}")
    public ResponseEntity<?> getAllUsersByUsername(@PathVariable("username") String ownerUsername) {
        try {
            List<Owner> users = new ArrayList<>(ownerRepository.findAllByUsernameContaining(ownerUsername));

            List<SearchAllOwnersRequest> saoList = new ArrayList<>();
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            for (Owner ow : users){
                SearchAllOwnersRequest sao = new SearchAllOwnersRequest(ow.getOwnerId(), ow.getUsername());
                saoList.add(sao);
            }

            SearchOwnerResponse searchOwnerResponse = new SearchOwnerResponse();
            searchOwnerResponse.setUsersFound(saoList);
            return new ResponseEntity<>(searchOwnerResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


    /** Find user by their id **/
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<Owner> getUsersById(@PathVariable("ownerId") long id) {
        Optional<Owner> userData = ownerRepository.findById(id);

        return userData.map(owner -> new ResponseEntity<>(owner, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** This function returns a single user and all the information about them.
     * In order to find a specific user, one must know the correct username. **/
    @GetMapping("/searchOwners/{username}")
    public ResponseEntity<Owner> getUserByUsername(@PathVariable("username") String ownerUsername) {
        Optional<Owner> userData = ownerRepository.findOwnerByUsername(ownerUsername);

        return userData.map(owner -> new ResponseEntity<>(owner, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Delete all users **/
    @DeleteMapping("/owners")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            ownerRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }

    /** Delete user by their id **/
    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("ownerId") long id) {
        try {
            ownerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
