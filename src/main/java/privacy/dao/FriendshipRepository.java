package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import privacy.models.Friendship;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findFriendshipsByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);

}
