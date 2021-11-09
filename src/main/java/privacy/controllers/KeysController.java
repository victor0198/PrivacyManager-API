package privacy.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import privacy.dao.KeysRepository;
import privacy.general.payload.request.KeyRequest;
import privacy.models.MyCredentials;
import privacy.models.MyKeys;
import privacy.registration.payload.response.MessageResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class KeysController {
    @Autowired
    private KeysRepository keysRepository;

    @PostMapping("/upload_my_key")
    public ResponseEntity<?> uploadKey(@RequestBody KeyRequest keyRequest) {
        if (keyRequest.getFileChecksum().isEmpty() || keyRequest.getFileKey().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid request body!"));
        }

        MyKeys key = new MyKeys(keyRequest.getKeyId(),
                keyRequest.getUserId(),
                keyRequest.getFileKey(),
                keyRequest.getFileChecksum());
        keysRepository.save(key);
        return ResponseEntity.ok(key);
    }
}
