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

        /**
         * @param userId - the list should display only the credentials that belong to the user with this id
         * @return a list containing information about the registered credentials
         */
        List<MyCredentials> findMyCredentialsByOwnerId(Long userId);

        void deleteMyCredentialsByOwnerIdAndCredentialId(Long userId, Long credentialId);



}
