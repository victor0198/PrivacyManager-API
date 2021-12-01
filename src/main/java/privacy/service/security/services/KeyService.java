package privacy.service.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import privacy.dao.KeyRepository;
import privacy.general.payload.request.KeyRequest;
import privacy.models.MyKeys;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyService {
    private final KeyRepository keyRepository;

    private final OwnerDetailsServiceImpl userDetailsService;

    /**
     * @param keyRequest - request to register new key
     * @return ResponseEntity with status 200, containing the info
     * inside an object of type MyKey
     */
    public MyKeys uploadKey(KeyRequest keyRequest) {
        MyKeys key = new MyKeys(keyRequest.getKeyId(),
                keyRequest.getUserId(),
                keyRequest.getFileKey(),
                keyRequest.getFileChecksum());
        keyRepository.save(key);
        return key;
    }

    /**
     * Function to enable the user to see all of his keys
     * @return a List containing info about an object of
     * type List(MyKeys) of the user's keys
     */
    public List getAllKeys() {
        List<MyKeys> keysList = new ArrayList<>();
        keysList.addAll(keyRepository.findByUserId(userDetailsService.getUserIdFromToken()));
        return keysList;
    }

    /**
     * Function to remove a key from cloud
     * @param id - the id of the key one intends to delete from cloud
     */
    public void deleteKey(@PathVariable("keyId") long id) {
        keyRepository.deleteById(id);
    }

    /**
     *
     * @param id of the user requesting the service
     * @return id of the user if found in the repository
     */
    public Long userIdFromRepo(Long id){
        return keyRepository.findByKeyId(id).get().getUserId();
    }
}
