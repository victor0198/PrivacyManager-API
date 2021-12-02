package privacy.service.security.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import privacy.dao.CloudKeysForSharingRepository;
import privacy.dao.FriendshipRepository;
import privacy.dao.SharedKeyRepository;
import privacy.general.payload.request.ShareKeyRequest;
import privacy.general.payload.response.KeysForMeResponse;
import privacy.models.CloudKeys;
import privacy.models.Friendship;
import privacy.models.SharedKeys;
import privacy.service.security.jwt.AuthEntryPointJwt;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedKeyService {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    
    private final OwnerDetailsServiceImpl ownerDetailsService;
    private final SharedKeyRepository sharedKeyRepository;
    private final CloudKeysForSharingRepository cloudKeysForSharingRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipService friendshipService;

    public void shareKey(ShareKeyRequest shareKeysRequest){
        CloudKeys cloudKey = new CloudKeys(ownerDetailsService.getUserIdFromToken(), shareKeysRequest.getKeyId(), shareKeysRequest.getFileKey(), shareKeysRequest.getFileChecksum());
        cloudKeysForSharingRepository.save(cloudKey);
        SharedKeys newlyCreatedCloudKey = new SharedKeys(cloudKey.getCloudKeyId(), shareKeysRequest.getFriendshipId());
        sharedKeyRepository.save(newlyCreatedCloudKey);
    }

    public KeysForMeResponse getKeysForMe() {
        logger.info("USER id:" + ownerDetailsService.getUserIdFromToken());
        List<Friendship> myFriendshipsTwo = friendshipRepository.findFriendshipsByUserTwoId(ownerDetailsService.getUserIdFromToken());
        List<Friendship> myFriendshipsOne = friendshipRepository.findFriendshipsByUserOneId(ownerDetailsService.getUserIdFromToken());
        logger.info("theFriendships:" + myFriendshipsOne.toString());
        logger.info("theFriendships:" + myFriendshipsTwo.toString());

        List<SharedKeys> allSharedKeys = new ArrayList<>();

        for (Friendship friendship:
                myFriendshipsOne) {
            List<SharedKeys> sharedKeys = sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());
            allSharedKeys.addAll(sharedKeys);
        }

        for (Friendship friendship:
                myFriendshipsTwo) {
            List<SharedKeys> sharedKeys = sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());
            allSharedKeys.addAll(sharedKeys);
        }
        logger.info("allSharedKeys:" + allSharedKeys.toString());

        List<CloudKeys> cloudKeys = new ArrayList<>();
        for (SharedKeys sharedKey:
                allSharedKeys) {
            CloudKeys cloudKey = cloudKeysForSharingRepository.findCloudKeysByCloudKeyId(sharedKey.getCloudKeyId());
            cloudKeys.add(cloudKey);
        }
        logger.info("cloudKeys:" + cloudKeys.toString());


        KeysForMeResponse keysForMeResponse = new KeysForMeResponse();
        if (cloudKeys.isEmpty()) {
            cloudKeys.add(new CloudKeys(0,0,"",""));
        }
        keysForMeResponse.setKeysForMe(cloudKeys);

        return keysForMeResponse;
    }

    /**
     * @param cloudKeyId - id of the key shared accross the cloud server
     * @return a list(SharedKey) of the results
     */
    public List<SharedKeys> getSharedKeyByCloudKeyId(Long cloudKeyId) {
        return sharedKeyRepository.findSharedKeysByCloudKeyId(cloudKeyId);
    }

    /**
     * @param user2Id - id of the user one tries to find the key they have shared with
     * @return a list(SharedKeys) of the keys shared with this second user
     */
    public List<SharedKeys> getSharedKeyByUser2Id(Long user2Id) {
        Friendship friendship = friendshipService.checkForFriendship(user2Id);

        return sharedKeyRepository.findSharedKeysByFriendshipId(friendship.getFriendshipId());

    }


    /**
     * @return a List with info about the user's friendships
     */
    public List<SharedKeys> getSharedKeyByCloudKeyIdOrFriendshipId(Long cloudKeyId, @PathVariable("friendshipId") Long friendshipId) {
        return sharedKeyRepository.findSharedKeysByCloudKeyIdEqualsAndFriendshipIdEquals(cloudKeyId, friendshipId);
    }


    /**
     * Request to clear the entire list of registered friendships
     */
    public void deleteAllKeys() {
        sharedKeyRepository.deleteAll();
    }

    /**
     * Function to remove a shared key that belongs to a specific user
     * @param id of the user
     */
    public void deleteFriendship(Long id) {
        sharedKeyRepository.deleteById(id);
    }

}
