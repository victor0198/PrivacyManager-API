package privacy.service.security.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import privacy.dao.FriendshipRequestsRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.response.FriendshipRequests;
import privacy.models.new_friend.FriendshipRequestCreated;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class FriendshipRequestsService {
    @Autowired
    private FriendshipRequestsRepository friendshipRequestsRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerDetailsServiceImpl ownerDetailsService;
    /**
     * @param frRequest - contains the senderID - automatically retrieved from JWT, the receiverID and the publicKey
     * @return a ResponseEntity containing info about an object of type FriendshipRequestCreated with
     * the senderID, the senderUsername, the receiverId and the publicKey
     */
    public Object registerRequest(FriendshipRequestCreated frRequest) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ownerRepository.findOwnerByUsername(currentUser).get().getOwnerId();
        frRequest.setSenderId(userId);
        frRequest.setSenderUsername(currentUser);

        boolean alreadySent = friendshipRequestsRepository.existsFriendshipRequestCreatedBySenderIdAndAndReceiverId(frRequest.getSenderId(), frRequest.getReceiverId());
        if (alreadySent) {
            return "Request has already been sent";
        } else if (friendshipRequestsRepository.existsFriendshipRequestCreatedBySenderIdAndAndReceiverId(frRequest.getReceiverId(), frRequest.getSenderId())) {
            return "Request has already been sent by the other user";
        } else if (frRequest.getSenderId() == frRequest.getReceiverId()){
            return "Sending request to oneself";
        }else {

            FriendshipRequestCreated friendshipRequestCreated = new FriendshipRequestCreated(frRequest.getCreatedRequestId(),
                    frRequest.getSenderId(),
                    frRequest.getSenderUsername(),
                    frRequest.getReceiverId(),
                    frRequest.getPublicKey());

            return friendshipRequestCreated;
        }

    }

    /**
     * @return a ResponseEntity with a list of objects of type FriendshipRequestCreated
     * displaying the friendship requests sent by the current user
     */
    public List<?> getAllSentFrRequests() {
        Long userId = ownerDetailsService.getUserIdFromToken();
        return new ArrayList<>(friendshipRequestsRepository.findBySenderId(userId));
    }

    /**
     * @return a ResponseEntity with a list FriendshipRequests containing objects of type FriendshipRequestCreated
     * displaying info about friendship requests received by the current user
     */
    public FriendshipRequests getAllReceivedFrRequests() {
        Long userId = ownerDetailsService.getUserIdFromToken();
        List<FriendshipRequestCreated> requestsList = new ArrayList<>();
        requestsList.addAll(friendshipRequestsRepository.findFriendshipRequestCreatedByReceiverId(userId));

        FriendshipRequests frReq = new FriendshipRequests();
        frReq.setNotificationsList(requestsList);

        return frReq;
    }
}
