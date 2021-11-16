package privacy.controllers;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
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
import privacy.models.RefreshToken;
import privacy.models.Role;
import privacy.registration.payload.request.LoginRequest;
import privacy.registration.payload.request.SignupRequest;
import privacy.registration.payload.response.JwtResponse;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.jwt.TokenRefreshException;
import privacy.service.security.jwt.payload.requests.TokenRefreshRequest;
import privacy.service.security.jwt.payload.responses.TokenRefreshResponse;
import privacy.service.security.services.OwnerDetailsImpl;
import privacy.service.security.jwt.JwtUtils;
import privacy.service.security.services.RefreshTokenService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final PasswordEncoder encoder;

    private final RefreshTokenService refreshTokenService;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final OwnerRepository ownerRepository;


    /**
     * @param loginRequest sent by an already signed-up user
     * @return a JWT: refresh the previously generated token
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        OwnerDetailsImpl userDetails = (OwnerDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(
                new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    /**
     * This functions checks the JWT's owner and expiration status and refreshes the token
     * @param request to refresh the JWT, containing info about the generated token for the user in the current session
     * @return the token with a reset timer for the user in the current session
     */
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getOwner)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    /**
     * @param signUpRequest contains information about the username and the password by which new users try to register
     * @return a ResponseEntity that contains the status of the processed request and information about the newly registered
     * user, if the request was successfully processed.
     */
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



}
