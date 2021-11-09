package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.MyKeys;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface KeysRepository extends JpaRepository<MyKeys, Long> {

    /** Find a key by its id **/
    Optional<MyKeys> findByKeyId(Long keyId);

}

