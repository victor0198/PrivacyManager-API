package privacy.controllers;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import privacy.service.security.OwnerDetailsImpl;
import privacy.service.security.jwt.JwtUtils;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/v1/authenticate", method = RequestMethod.POST)
public class AuthenticationController {
    private static final org.slf4j.Logger Logger= LoggerFactory.getLogger(AuthenticationController.class);

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
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    /** Sign up request
     * To do: The new Owner(...) returns an error that need to be solved **/
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (ownerRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (ownerRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        /** Create new user's account **/
        Owner user = new Owner(signUpRequest.getUsername(),signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Logger.info(signUpRequest.getUsername());
        String strRoles = signUpRequest.getRole();
        Logger.info(strRoles);

        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
            Logger.info("inside null signup");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            user.setRoles(roles);
            Owner user1 = ownerRepository.save(user);
            Owner nullUser = ownerRepository.findByEmail(user1.getEmail());
            ownerRepository.save(new Owner(nullUser.getUsername(), nullUser.getEmail(), nullUser.getPassword()));

        } else if (strRoles.equals("admin")) {
            Logger.info("inside else signup");

            /** If one signs up as administrator, no username, email or password is required **/
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            user.setRoles(roles);
            ownerRepository.save(user);

        }
        else {
            /** If one signs up as user, they are required a username, an email and a password **/
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            Logger.info("inside user "+strRoles);
            roles.add(userRole);
            user.setRoles(roles);
            Owner user1 = ownerRepository.save(user);
            Owner nullOwner=ownerRepository.findByEmail(user1.getEmail());
            ownerRepository.save(new Owner(nullOwner.getUsername(), nullOwner.getEmail(), nullOwner.getPassword()));
        }


//		strRoles.forEach(role -> {
//			System.out.println("inside not null");
//			switch (role) {
//			case "admin":
//				Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//				roles.add(adminRole);
//
//				break;
//			default:
//				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//				System.out.println("inside user");
//				roles.add(userRole);
//			}
//		});
//
        Logger.info("outside");
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /** Get information about all users **/
    @GetMapping("/owners")
    public ResponseEntity<List<Owner>> getAllUsers() {
        List<Owner> users = new ArrayList<>();
        try {
            ownerRepository.findAll().forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
