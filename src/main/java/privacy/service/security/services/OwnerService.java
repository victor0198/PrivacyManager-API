package privacy.service.security.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import privacy.dao.OwnerRepository;
import privacy.general.payload.request.SearchAllOwnersRequest;
import privacy.general.payload.response.SearchOwnerResponse;
import privacy.models.Owner;
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OwnerService {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final OwnerRepository ownerRepository;

    /**
     * @return a list containing objects of type Owner to display all registered users
     */
    public List<Owner> getAllUsers() {
        return new ArrayList<>(ownerRepository.findAll());
    }

    /**
     * This function returns a list of all possible matches for the username being searched.
     * * It comes in handy when one doesn't know the entire username of the sought user.
     * * In return to the search request, this function returns a list of usernames (only).
     * * If the one searching found the username they needed, they may proceed to use the /searchOwners/{username}
     * * endpoint to get a full description of the specified user and its username.
     *
     * @param ownerUsername - the searched user's possible username or a slice of it
     * @return a ResponseEntity containing info about the user being searched, when found
     */
    public SearchOwnerResponse getAllUsersByUsername(String ownerUsername) {
        List<Owner> users = new ArrayList<>(ownerRepository.findAllByUsernameContaining(ownerUsername));

        List<SearchAllOwnersRequest> saoList = new ArrayList<>();

        if (users.isEmpty()) {
            return new SearchOwnerResponse();
        }

        for (Owner ow : users) {
            SearchAllOwnersRequest sao = new SearchAllOwnersRequest(ow.getOwnerId(), ow.getUsername());
            saoList.add(sao);
        }

        SearchOwnerResponse searchOwnerResponse = new SearchOwnerResponse();
        searchOwnerResponse.setUsersFound(saoList);
        return searchOwnerResponse;
    }


    /**
     * @param id of the owner being searched
     * @return a ResponseEntity containing an object of type Owner with info about the user being searched, when found
     */
    public Optional<Owner> getUsersById(long id) {
        return ownerRepository.findById(id);
    }

    /**
     * Search user by their username. In order to find a specific user, one must know the correct username.
     *
     * @param ownerUsername - the username by which the search is performed
     * @return a single user and all the information about them.
     */
    public Optional<Owner> getUserByUsername(String ownerUsername) {
        return ownerRepository.findOwnerByUsername(ownerUsername);
    }

    /**
     * Request to clear the entire list of registered user
     */
    public void deleteAllUsers() {
        ownerRepository.deleteAll();
    }

    /**
     * Function to remove a user
     *
     * @param id of the user
     */
    public void deleteUser(long id) {
        ownerRepository.deleteById(id);
    }
}
