package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.Friendship;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Friendship findFriendshipByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);
    List<Friendship> findFriendshipsByUserOneIdEqualsOrUserTwoIdEquals(Long userOneId, Long userTwoId);

//    Optional<Friendship> findFriendshipsByFriendshipId(Long friendshipId);
}
