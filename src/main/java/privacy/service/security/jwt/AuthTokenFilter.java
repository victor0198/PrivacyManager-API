package privacy.service.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import privacy.service.security.services.OwnerDetailsServiceImpl;

/** This class makes a single execution for each request to our API. It provides a doFilterInternal() method
 * that we will implement parsing & validating JWT, loading User details (using UserDetailsService), checking
 * Authorization (using UsernamePasswordAuthenticationToken). */
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OwnerDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * The function below enables us to:
     *      *  – get JWT from the Authorization header (by removing Bearer prefix);
     *      – if the request has JWT, validate it, parse username from it;
     *      – from username, get UserDetails to create an Authentication object;
     *      – set the current UserDetails in SecurityContext using setAuthentication(authentication) method.
     * @param request to pass along the chain
     * @param response the response to pass along the chain
     * @param filterChain - an object provided by the servlet container to the developer giving a view into
     *                    the invocation chain of a filtered request for a resource.
     * @throws ServletException if the service failed to process the request to parse the JWT
     * @throws IOException  if an I/O error occurs during the processing of the request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: "+e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     *
     * @param request to provide information for HTTP servlets
     * @return the header's first 7 characters that contain info about the JWT
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}

