package privacy.old;

import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.util.function.Predicate;

@Service
public class EmailValidator{
    //    private static Pattern EMAIL_REGEX= Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern EMAIL_REGEX= Pattern.compile("^[a-z0-9]+.?[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,6}$");
    //    @Override
    public boolean test(String email) {
        final Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.matches();
    }


}
