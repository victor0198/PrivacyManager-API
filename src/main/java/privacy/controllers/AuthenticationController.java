package privacy.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import privacy.dao.OwnerRepository;
import privacy.dao.RoleRepository;
import privacy.models.ERole;
import privacy.models.Owner;
import privacy.models.Role;
import privacy.registration.payload.request.LoginRequest;
import privacy.registration.payload.request.SignupRequest;
import privacy.registration.payload.response.JwtResponse;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.services.OwnerDetailsImpl;
import privacy.service.security.jwt.JwtUtils;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/** The controller receives and handles a request after it was filtered by OncePerRequestFilter.

 > AuthController handles signup/login requests

 – /api/auth/signup
    >>check existing username/email
    >>create new User (as USER if there is no specified role)
    >>save User to database using UserRepository

 – /api/auth/signin
    >>authenticate { username, password }
    >>update SecurityContext using Authentication object
    >>generate JWT
    >>get UserDetails from Authentication object
    >>the response contains JWT and UserDetails data**/
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
//    private static final org.slf4j.Logger Logger= LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OwnerRepository ownerRepository;


    /** Sign in request **/
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        OwnerDetailsImpl userDetails = (OwnerDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    /** Sign up request **/
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (ownerRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        Owner user = new Owner(signUpRequest.getUsername(),signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        ownerRepository.save(user);
        return ResponseEntity.ok(user);
    }

    /** Get information about all users **/
    @GetMapping("/owners")
    public ResponseEntity<List<Owner>> getAllUsers() {
        try {
            List<Owner> users = new ArrayList<>(ownerRepository.findAll());

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    /** Delete all users **/
    @DeleteMapping("/owners")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            ownerRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }
//    @PostMapping
//    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtil.generateToken(authentication);
//
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
//        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
//    }

    /** Find user by their id **/
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<Owner> getUsersById(@PathVariable("ownerId") long id) {
        Optional<Owner> userData = ownerRepository.findById(id);

        return userData.map(owner -> new ResponseEntity<>(owner, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Delete user by their id **/
    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("ownerId") long id) {
        try {
            ownerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


}
