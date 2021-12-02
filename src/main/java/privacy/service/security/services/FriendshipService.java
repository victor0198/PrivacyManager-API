package privacy.service.security.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import privacy.dao.FriendshipRepository;
import privacy.dao.OwnerRepository;
import privacy.models.Friendship;
import privacy.registration.payload.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@NoArgsConstructor
//@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class FriendshipService {
    private final OwnerRepository ownerRepository;
    private final OwnerDetailsServiceImpl ownerDetailsService;
    private final FriendshipRepository friendshipRepository;



    /**
     * @return a list containing objects of type Friendship to display all registered friendships
     */
    public List<?> getAllFriendships() {
        return new ArrayList<>(friendshipRepository.findAll());
    }

    /**
     *
     * @param frTwoId - id of the user one tries to connect with
     * @return a Friendship object
     */
    public Friendship checkForFriendship(Long frTwoId){
        Optional<Friendship> try1 = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(ownerDetailsService.getUserIdFromToken(), frTwoId);
        Optional<Friendship> try2 = friendshipRepository.findFriendshipByUserOneIdAndUserTwoId(frTwoId,ownerDetailsService.getUserIdFromToken());
        if (try1.isPresent()){
            return try1.get();
        } else if (try2.isPresent()){
            return try2.get();
        } else{
            return null;
        }
    }

    /**
     * @return a ResponseEntity containing an object of type List with info about the user's friendships
     */
    public List<?> getFriendshipById() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        return friendshipRepository.findFriendshipsByUserOneIdEqualsOrUserTwoIdEquals(userId, userId);
    }


    /**
     * Request to clear the entire list of registered friendships
     * @return a ResponseEntity informing about the delete request status
     */
    public void deleteAllFriendships() {
        friendshipRepository.deleteAll();
    }

    /**
     * Function to remove a friendship that belongs to a specific user
     * @param id of the user
     * @return a ResponseEntity informing about the delete request status
     */
    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }

}
