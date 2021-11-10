package privacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import privacy.models.Owner;
import privacy.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByOwnerId(Long id);

    Optional<RefreshToken> findByToken(String token);

    int deleteByOwner(Owner owner);
}
