package in.fincase.jwt;

import java.util.Date;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class JWTTokenUtil {

    // Secret key for token signing
    private final String secretSignInKey ="fincasesecretsignkeyforipoallotment";
    /**
     * Creates a JWT token for a given user ID.
     *
     * @param userId The ID of the user
     * @return A signed JWT token
     */
    public String createToken(String userId) {
        try {
            log.info("Generating token for user ID: {}", userId);
            System.out.println("creating token");
            return Jwts.builder()
                    .setSubject(userId)
                    .setIssuer("Fincase Application")
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(100))) // Expire in 5 mins
                    .signWith(SignatureAlgorithm.HS256, secretSignInKey)
                    .compact();
        } catch (Exception e) {
            log.error("Error while generating token for user ID: {} - {}", userId, e.getMessage());
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token JWT token string
     * @return Username embedded in the token
     */
    public String getUserNameFromToken(String token) {
        try {
            Claims claims = parseTokenClaims(token);
            String userName = claims.getSubject();

            if (userName == null || userName.trim().isEmpty()) {
                log.warn("Token does not contain a valid username.");
                throw new IllegalArgumentException("Invalid token: Missing username.");
            }

            log.debug("Extracted username '{}' from token", userName);
            return userName;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to parse username from token.", e);
        }
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token JWT token string
     * @return True if the token is expired, otherwise false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseTokenClaims(token);
            Date expirationDate = claims.getExpiration();

            boolean isExpired = expirationDate.before(new Date());
            if (isExpired) {
                log.warn("Token is expired. Expiration time: {}", expirationDate);
            }

            return isExpired;
        } catch (JwtException e) {
            log.error("Failed to check token expiration: {}", e.getMessage());
            return true; // Treat failures as expired tokens for security reasons
        }
    }

    /**
     * Validates a token by username and expiry status.
     *
     * @param requestedUserName Username to validate against
     * @param token JWT token string
     * @return True if the token is valid, otherwise false
     */
    public boolean isTokenValid(String requestedUserName, String token) {
        try {
            String tokenUserName = getUserNameFromToken(token);
            boolean isExpired = isTokenExpired(token);

            boolean isValid = !isExpired && tokenUserName.equals(requestedUserName);

            if (isValid) {
                log.info("Token is valid for user: {}", requestedUserName);
            } else {
                log.warn("Token is invalid or expired for user: {}", requestedUserName);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parses the token claims for further validation.
     *
     * @param token JWT token string
     * @return Parsed claims
     */
    private Claims parseTokenClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Attempting to parse a null or empty token.");
            throw new IllegalArgumentException("Token is null or empty.");
        }

        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKey(secretSignInKey)
                    .parseClaimsJws(token);

            return jwsClaims.getBody();
        } catch (JwtException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }
}
