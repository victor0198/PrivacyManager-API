package privacy.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import privacy.dao.OwnerRepository;
import privacy.models.Owner;

public class OwnerDetailsServiceImpl implements UserDetailsService {
    @Autowired
    OwnerRepository ownerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.findOwnerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return OwnerDetailsImpl.build(owner);
    }
}
