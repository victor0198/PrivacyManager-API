package privacy.service.security.services.imap;//package privacy.service.security.services.imap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

//@Service
@Getter
@Setter
@AllArgsConstructor
public class EmailReceiver {

    String textEmail;

    public void retrieveEmail() throws MessagingException, IOException {
        int messageIdx = 1;
        Scanner sc = new Scanner(System.in);
        boolean continueRead = true;
        while(continueRead){
            readLastMessage(messageIdx);
            messageIdx+=1;
            this.setTextEmail("\nGet next message?  y/n");
            String ans = sc.nextLine().toString();
            if(Objects.equals(ans, "y")){
                this.setTextEmail("Message index: "+messageIdx+"\n");
            }else{
                continueRead=false;
                this.setTextEmail("Ok, see you next time.\n");
            }
        }
    }

    public void readLastMessage(int idx) throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);


        String username = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        Properties prop = new Properties();
        prop.put("mail.store.protocol", "imaps");  //SSL
        Store store = Session.getInstance(prop).getStore();
        store.connect(host, username, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        System.out.println("Number of emails: "+inbox.getMessageCount());

        Message message = inbox.getMessage(inbox.getMessageCount());
//        File emailFile = new File();

        if(message.isMimeType("text/plain")){
            FileWriter emailWriter = new FileWriter("N:\\MINE\\PR\\Lab2\\email_file.txt");
            emailWriter.write(message.getContent().toString());
            emailWriter.close();
            System.out.println("New email: "+ message.getContent().toString());

        }else{
            Multipart multipart = (Multipart) message.getContent();
            BodyPart body = multipart.getBodyPart(idx);
            FileWriter emailWriter = new FileWriter("N:\\MINE\\PR\\Lab2\\email_file.html");
            emailWriter.write(body.getContent().toString());
            emailWriter.close();
            System.out.println("Check for html file\n");
        }

        message.setFlag(Flags.Flag.SEEN, true);
        inbox.close();
    }
}
