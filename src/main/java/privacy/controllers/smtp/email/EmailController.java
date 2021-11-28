package privacy.controllers.smtp.email;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import privacy.general.payload.request.EmailRequest;
import privacy.service.security.services.MailService;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailController {
    private final MailService mailService;

    @RequestMapping(value = "/sendemail")
    public String sendMail(@RequestBody EmailRequest emailRequest) throws AddressException, MessagingException, IOException {
        mailService.sendMail(
                emailRequest.getSubject(),
                emailRequest.getToAddresses(),
                emailRequest.getCcAddresses(),
                emailRequest.getBccAddresses(),
                emailRequest.getBody());
        return "Email sent successfully";
    }
}
