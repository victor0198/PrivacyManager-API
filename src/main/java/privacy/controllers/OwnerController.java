package privacy.controllers;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import privacy.service.security.OwnerDetailsServiceImpl;

/** This controller will eventually be safely deleted **/

@RestController
@RequestMapping(path = "api/v1/owner")
@AllArgsConstructor
public class OwnerController {

    private final OwnerDetailsServiceImpl ownerService;
//
//    @GetMapping
//    public List<Owner>  getAllOwners(){return ownerService.getAllOwners();}
//
//    @PostMapping
//    public void signUpOwner(@RequestBody Owner owner) throws JSONException {
//        if(ownerService.getAllOwners().contains(owner)){
//            throw new IllegalStateException("Owner exists!");
//        }
//        ownerService.signUpOwner(owner);
//    };
}
