package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.SharedKeys;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface SharedKeyRepository extends JpaRepository<SharedKeys, Long> {

    List<SharedKeys> findSharedKeysByCloudKeyId(Long cloudKeyId);

    List<SharedKeys> findSharedKeysByCloudKeyIdEqualsAndFriendshipIdEquals(Long cloudKeyId, Long friendshipId);

    List<SharedKeys> findSharedKeysByFriendshipId(Long friendshipId);
}
