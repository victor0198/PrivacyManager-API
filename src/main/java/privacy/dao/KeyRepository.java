package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.MyKeys;
import privacy.models.new_friend.FriendshipRequestCreated;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface KeyRepository extends JpaRepository<MyKeys, Long> {

    Optional<MyKeys> findByKeyId(Long keyId);

    List<MyKeys> findByUserId(long userId);

}

