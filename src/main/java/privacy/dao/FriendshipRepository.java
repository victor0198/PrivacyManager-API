package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.Friendship;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findFriendshipByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);
    List<Friendship> findFriendshipsByUserOneIdEqualsOrUserTwoIdEquals(Long userOneId, Long userTwoId);
    List<Friendship> findFriendshipsByUserTwoId(Long userTwoId);
    List<Friendship> findFriendshipsByUserOneId(Long userTwoId);

//    Optional<Friendship> findFriendshipsByFriendshipId(Long friendshipId);
}
