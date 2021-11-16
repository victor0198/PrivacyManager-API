package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.dao.KeyRepository;
import privacy.dao.OwnerRepository;
import privacy.general.payload.request.KeyRequest;
import privacy.models.MyKeys;
import privacy.registration.payload.response.MessageResponse;
import privacy.service.security.jwt.AuthEntryPointJwt;
import privacy.service.security.services.OwnerDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KeyController {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private final KeyRepository keyRepository;

    private final OwnerDetailsServiceImpl userDetailsService;

    /**
     * @param keyRequest - request to register new key
     * @return ResponseEntity with status 200, containing the info
     * inside an object of type MyKey
     */
    @PostMapping("/new_key")
    public ResponseEntity<?> uploadKey(@RequestBody KeyRequest keyRequest) {
        if (keyRequest.getFileChecksum().isEmpty() || keyRequest.getFileKey().isEmpty()) {
            logger.warn("Invalid request body");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid request body!"));
        }

        if(userDetailsService.getUserIdFromToken() != keyRequest.getUserId()) {
            logger.warn("User is not authorized");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not authorized to perform this action!"));
        }

        MyKeys key = new MyKeys(keyRequest.getKeyId(),
                keyRequest.getUserId(),
                keyRequest.getFileKey(),
                keyRequest.getFileChecksum());
        logger.info("Saved key: "+key + "\nfor user "+ keyRequest.getUserId());
        keyRepository.save(key);
        return ResponseEntity.ok(key);
    }

    /**
     * Function to enable the user to see all of his keys
     * @return ResponseEntity containing info about an object of
     * type List of MyKeys
     */
    @GetMapping("/my_keys")
    public ResponseEntity<?> getAllKeys() {

        List<MyKeys> keysList = new ArrayList<>();
        try {
            keysList.addAll(keyRepository.findByUserId(userDetailsService.getUserIdFromToken()));

            if (keysList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(keysList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Couldn't retrieve keys for user "+userDetailsService.getUserIdFromToken());
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    /**
     * Function to remove a key from cloud
     * @param id - the id of the key one intends to delete from cloud
     * @return a ResponseEntity
     */
    @DeleteMapping("/my_keys/{keyId}")
    public ResponseEntity<?> deleteKey(@PathVariable("keyId") long id) {
        if(userDetailsService.getUserIdFromToken() != keyRepository.findByKeyId(id).get().getUserId()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not authorized to perform this action!"));
        }
        try {
            keyRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Key removed"));
        } catch (Exception e) {
            logger.error("Error with status 417 for user "+userDetailsService.getUserIdFromToken());
            return ResponseEntity.status(417).body("Couldn't process request");
        }
    }

}
