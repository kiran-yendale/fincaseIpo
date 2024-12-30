package in.fincase.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.fincase.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTTokenUtil tokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                validateAndAuthenticateToken(token);
            } catch (Exception e) {
                log.error("Error during token validation or authentication: {}", e.getMessage(), e);
                // Optional: Customize response for invalid token
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            log.debug("No Authorization token provided in the request");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the Bearer token from the Authorization header.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        } else {
            if (authorizationHeader == null) {
                log.warn("Missing Authorization header in the request");
            } else {
                log.warn("Invalid Authorization header format: {}", authorizationHeader);
            }
            return null;
        }
    }

    /**
     * Validates the token and sets up the user in the SecurityContext.
     */
    private void validateAndAuthenticateToken(String token) {
        String username = tokenUtil.getUserNameFromToken(token);

        if (username == null || username.isEmpty()) {
            log.warn("Invalid token: Unable to extract username");
            throw new IllegalArgumentException("Invalid token");
        }

        log.debug("Validating token for username: {}", username);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (!tokenUtil.isTokenValid(userDetails.getUsername(), token)) {
                log.warn("Token validation failed for user: {}", username);
                throw new SecurityException("Invalid or expired token");
            }

            log.info("Token validated successfully for user: {}", username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            log.debug("Authentication already exists for user: {}", username);
        }
    }
}