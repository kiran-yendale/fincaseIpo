package in.fincase.restcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import in.fincase.dto.Userdto;
import in.fincase.jwt.JWTTokenUtil;
import in.fincase.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Validated // To enable validation of method arguments
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    
    @Autowired 
    JWTTokenUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    

    @PostMapping("/register")
    public ResponseEntity<String> userRegistration(@RequestBody @Valid Userdto request) {
        String result = userService.saveuser(request);
        logger.info("User successfully registered with email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody @Valid Userdto request) {
        logger.debug("Starting authentication for user with email: {}", request.getEmail());
        doAuthenticationOfUser(request.getEmail(), request.getPassword());
        logger.debug("Authentication successful, creating token for user with email: {}", request.getEmail());
        
        String token = jwtUtil.createToken(request.getEmail());
        return ResponseEntity.ok().header("Authorization", "Bearer " + token)
                .body("Login successful");
    }

    private void doAuthenticationOfUser(String emailId, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(emailId, password);
        logger.debug("Attempting to authenticate user with token: {}", token);

        try {
            authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", emailId);
            throw new RuntimeException("Invalid credentials. Please provide valid login details.");
        }
    }
}
