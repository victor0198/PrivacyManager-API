package privacy.service.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import privacy.dao.OwnerRepository;
import privacy.models.Owner;
import privacy.service.security.services.OwnerDetailsImpl;

/** The UserDetailsService interface has a method to load User by username and returns a UserDetails object
 * that Spring Security can use for authentication and validation. **/
@Service
@RequiredArgsConstructor
public class OwnerDetailsServiceImpl implements UserDetailsService {

    private final OwnerRepository ownerRepository;

    /** We override the loadUserByName method and get a full custom User object using UserRepository,
     * then we build a UserDetails object using static build() method. **/
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.findOwnerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return OwnerDetailsImpl.build(owner);
    }
}
