package privacy.service.security.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import privacy.dao.OwnerRepository;

//@RequiredArgsConstructor //Didn't use this, because it doesn't allow an empty constructor to be initialised, whilst I need it to
@NoArgsConstructor
@AllArgsConstructor
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerDetailsServiceImpl userDetailsService;

    @Autowired
    @Qualifier("gmail")
    private JavaMailSender mailSender;

    public void sendMail(String subject, String toAddresses, String ccAddresses, String bccAddresses, String body) {
        String from = ownerRepository.findById(userDetailsService.getUserIdFromToken()).get().getUsername();
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(toAddresses.split("[,;]"));
            message.setFrom(from, "<From Name>");
            message.setSubject(subject);
            if (StringUtils.isNotBlank(ccAddresses))
                message.setCc(ccAddresses.split("[;,]"));
            if (StringUtils.isNotBlank(bccAddresses))
                message.setBcc(bccAddresses.split("[;,]"));
            message.setText(body, false); //or true, for multimedia emails
        };
        mailSender.send(preparator);
        logger.info("Email sent successfully To {},{} with Subject {}", toAddresses, ccAddresses, subject);
    }
}
