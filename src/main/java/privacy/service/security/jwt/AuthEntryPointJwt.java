package privacy.service.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** AuthEntryPointJwt class implements AuthenticationEntryPoint interface.
 * We override the commence() method. This method will be triggered anytime
 * unauthenticated User requests a secured HTTP resource and an AuthenticationException is thrown. **/
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /** This class will catch authentication error. In case an error occurs, the method
     * below outputs the error message **/
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Map<String, Object> body = new HashMap<>();

        /** HttpServletResponse.SC_UNAUTHORIZED is the 401 Status code.
         * It indicates that the request requires HTTP authentication. **/
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);

        /** The commented line below is a shortcut to the method above. **/
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }


}
