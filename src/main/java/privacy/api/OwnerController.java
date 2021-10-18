package privacy.api;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;
import privacy.models.Owner;
import privacy.service.OwnerService;

import java.util.List;


@RestController
@RequestMapping(path = "api/v1/owner")
@AllArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public List<Owner>  getAllOwners(){return ownerService.getAllOwners();}

    @PostMapping
    public void signUpOwner(@RequestBody Owner owner) throws JSONException {
        if(ownerService.getAllOwners().contains(owner)){
            throw new IllegalStateException("Owner exists!");
        }
        ownerService.signUpOwner(owner);
    };
}
