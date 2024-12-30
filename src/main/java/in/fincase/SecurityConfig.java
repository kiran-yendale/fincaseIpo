package in.fincase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import in.fincase.jwt.JWTAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
@Autowired
private JWTAuthenticationFilter jwtAuthenticationFilter;
	
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Instantiating AuthenticationManager");
        try {
            return config.getAuthenticationManager();
        } catch (Exception e) {
            logger.error("Failed to instantiate AuthenticationManager: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity security) {
        logger.info("Creating SecurityFilterChain configuration");
        try {
            security.csrf(csrf -> csrf.disable())
                   .authorizeHttpRequests(auth -> auth
                           .requestMatchers("/user/login", "/user/register", "/swagger-ui/*","/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**")
                           .permitAll()
                           .anyRequest()
                           .authenticated())
                   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return security.build();
        } catch (Exception e) {
            logger.error("Exception occurred while configuring SecurityFilterChain: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure SecurityFilterChain", e);
        }
    }

	
	}

