package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.CloudKeys;
import privacy.models.MyKeys;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CloudKeysForSharingRepository extends JpaRepository<CloudKeys, Long> {
    Optional<CloudKeys> findCloudKeysByKeyId(Long keyId);

    CloudKeys findCloudKeysByCloudKeyId(Long cloudKeyId);
}
