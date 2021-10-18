package privacy.service;
import lombok.AllArgsConstructor;
import org.hibernate.service.NullServiceException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import privacy.dao.OwnerRepository;
import privacy.models.Owner;

import java.util.List;

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

    public UserDetails findOwnerById(Long id) throws Exception {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new Exception("User " + id + " not found"));
    }

    public List<Owner> getAllOwners(){

        return ownerRepository.findAll();
    }

    public JSONObject signUpOwner(Owner owner) throws JSONException {
        boolean ownerExist = ownerRepository.findOwnerByUsername(owner.getUsername()).isPresent();
        if(ownerExist){
            throw new IllegalStateException("username taken");
        }

        String encodedPasword = bCryptPasswordEncoder.encode(owner.getPassword());
        owner.setPassword(encodedPasword);

        ownerRepository.save(owner);

        JSONObject response = new JSONObject();
        response.put("ownerId", owner.getId());

        return response;
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null; //ownerRepository.findOne(username);
//    }
}
