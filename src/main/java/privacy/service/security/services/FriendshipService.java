package privacy.service.security.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import privacy.dao.FriendshipRepository;
import privacy.dao.OwnerRepository;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
//@AllArgsConstructor
@AllArgsConstructor
@Service
public class FriendshipService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    /**
     * @return a list containing objects of type Friendship to display all registered friendships
     */
    public List<?> getAllFriendships() {
        return new ArrayList<>(friendshipRepository.findAll());
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
