package privacy.dao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.Owner;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findById(Long ownerId);

    Optional<Owner> findOwnerByUsername(String ownerUsername);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
