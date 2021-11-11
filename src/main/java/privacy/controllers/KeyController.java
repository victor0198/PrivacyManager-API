package privacy.controllers;

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
import privacy.service.security.services.OwnerDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class KeyController {
    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private OwnerDetailsServiceImpl userDetailsService;

    @PostMapping("/new_key")
    public ResponseEntity<?> uploadKey(@RequestBody KeyRequest keyRequest) {
        if (keyRequest.getFileChecksum().isEmpty() || keyRequest.getFileKey().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid request body!"));
        }

        if(userDetailsService.getUserIdFromToken() != keyRequest.getUserId()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not authorized to perform this action!"));
        }

        MyKeys key = new MyKeys(keyRequest.getKeyId(),
                keyRequest.getUserId(),
                keyRequest.getFileKey(),
                keyRequest.getFileChecksum());
        keyRepository.save(key);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/my_keys")
    public ResponseEntity<List<MyKeys>> getAllKeys() {

        List<MyKeys> keysList = new ArrayList<>();
        try {
            keysList.addAll(keyRepository.findByUserId(userDetailsService.getUserIdFromToken()));

            if (keysList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(keysList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/my_keys/{keyId}")
    public ResponseEntity<?> deleteKey(@PathVariable("keyId") long id) {
        if(userDetailsService.getUserIdFromToken() != keyRepository.findByKeyId(id).get().getUserId()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not authorized to perform this action!"));
        }
        try {
            keyRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

}
