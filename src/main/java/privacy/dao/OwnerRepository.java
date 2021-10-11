package privacy.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.Owner;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findOwnerByEmail(String ownerName);

}
