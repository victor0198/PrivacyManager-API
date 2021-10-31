package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import privacy.models.MyCredentials;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CredentialsRepository extends JpaRepository<MyCredentials, Long> {
        boolean existsByService(String service);

        /** Find a credential by its id **/
        Optional<MyCredentials> findById(Long credentialId);

        /** View all the specified user's credentials **/
        List<MyCredentials> findByUserId(Long userId);


}
