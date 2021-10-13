package privacy.service;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import privacy.dao.OwnerRepository;
import privacy.models.Owner;

@Service
@AllArgsConstructor
public class OwnerService implements UserDetailsService {

    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return ownerRepository.findOwnerByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }

    public JSONObject signUpOwner(Owner owner) throws JSONException {
        boolean ownerExist = ownerRepository.findOwnerByEmail(owner.getEmail()).isPresent();
        if(ownerExist){
            throw new IllegalStateException("email taken");
        }

        String encodedPasword = bCryptPasswordEncoder.encode(owner.getPassword());
//        String encodedPasword = owner.getPassword();
        owner.setPassword(encodedPasword);

        ownerRepository.save(owner);

        JSONObject response = new JSONObject();
        response.put("owner_id", owner.getId());

        return response;
    }

}
