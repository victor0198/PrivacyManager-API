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
    Optional<Owner> findOwnerById(Long ownerId);
    Optional<Owner> findOwnerByEmail(String ownerEmail);

    Optional<Owner> findOwnerByUsername(String ownerUsername);
//
//    List<Owner> findAll(int i, int i1);

//    Optional<Owner> findOwnerBy

}
