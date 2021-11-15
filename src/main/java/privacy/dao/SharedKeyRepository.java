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
    Optional<SharedKeys> findAllByFriendshipId(Long friendshipId);

    List<SharedKeys> findByCloudKeyId(long cloudKeyId);
}
