package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.new_friend.FriendshipRequestCreated;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FriendshipRequestsRepository extends JpaRepository<FriendshipRequestCreated, Long>{
    List<FriendshipRequestCreated> findBySenderId(long senderId);

}
