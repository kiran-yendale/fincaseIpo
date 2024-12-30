package in.fincase.serviceimpl;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import in.fincase.dto.Userdto;
import in.fincase.entity.UserEntity;
import in.fincase.mapper.UserMapper;
import in.fincase.repository.UserRepository;
import in.fincase.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService ,UserDetailsService{
 @Autowired
 private UserRepository userRepo;
 
 @Autowired
 private UserMapper usermapper;
	
 @Autowired
 BCryptPasswordEncoder passwordEncoder;
 @Override
 public String saveuser(Userdto user) {
	   log.info("Initiating user registration for user email: {}", user.getEmail());

	    // Check if user already exists by email
	    if (userRepo.findByEmail(user.getEmail()).isPresent()) {
	        log.warn("Registration attempt failed: User with email {} is already registered.", user.getEmail());
	        throw new IllegalArgumentException("User with the provided email is already registered.");
	    }
	    
     try {
         // Encrypt password and map to entity
         String encryptedPass = passwordEncoder.encode(user.getPassword());
         UserEntity userEntity = usermapper.toEntity(user);
         userEntity.setPassword(encryptedPass);

         userRepo.save(userEntity);
         log.info("User registration completed for user : {}", user.getEmail());
         return "User registration succeess";
     } catch (Exception e) {
         log.error("Error during user registration: {}", e.getMessage(), e);
         throw new ServiceException("Failed to register user", e);
     }
	
 }

 @Override
 @Cacheable("users")
 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     log.info("Looking up user by userMail: {}", username);

     return userRepo.findByEmail(username)
         .orElseThrow(() -> {
             log.warn("No user found for username: {}", username);
             return new UsernameNotFoundException("User not found");
         });
 }

}
