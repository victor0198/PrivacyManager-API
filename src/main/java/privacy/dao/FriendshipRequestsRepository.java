package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.new_friend.FriendshipRequestAccepted;
import privacy.models.new_friend.FriendshipRequestCreated;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface FriendshipRequestsRepository extends JpaRepository<FriendshipRequestCreated, Long>{
    List<FriendshipRequestCreated> findBySenderId(long senderId);

    List<FriendshipRequestCreated> findFriendshipRequestCreatedByReceiverId(long receiverId);

}
