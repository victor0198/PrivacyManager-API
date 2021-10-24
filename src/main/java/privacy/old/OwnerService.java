package privacy.old;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import privacy.dao.OwnerRepository;

@Service
@AllArgsConstructor
public class OwnerService implements UserDetailsService {

//    private final OwnerRepository ownerRepository;
    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final org.slf4j.Logger Logger= LoggerFactory.getLogger(OwnerService.class);
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return ownerRepository.findOwnerByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }
//
//    public UserDetails findOwnerById(Long id) throws Exception {
//        return ownerRepository.findById(id)
//                .orElseThrow(() -> new Exception("User " + id + " not found"));
//    }
//
//    public List<Owner> getAllOwners(){
//
//        return ownerRepository.findAll();
//    }

//    public JSONObject signUpOwner(Owner owner) throws JSONException {
//        boolean ownerExist = ownerRepository.findOwnerByUsername(owner.getUsername()).isPresent();
//        if(ownerExist){
//            throw new IllegalStateException("username taken");
//        }
//
//        String encodedPasword = bCryptPasswordEncoder.encode(owner.getPassword());
//        owner.setPassword(encodedPasword);
//
//        ownerRepository.save(owner);
//
//        JSONObject response = new JSONObject();
//        response.put("ownerId", owner.getId());
//
//        return response;
//    }
//}

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null; //ownerRepository.findOne(username);
//    }
}
