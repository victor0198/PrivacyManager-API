package privacy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import privacy.dao.CloudKeysForSharingRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CloudKeysController {
    private final CloudKeysForSharingRepository cloudKeysForSharingRepository;

}
