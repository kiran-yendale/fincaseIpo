package in.fincase.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import in.fincase.dto.Userdto;

@Service
public interface UserService {
	public String saveuser(Userdto user);
    public UserDetails 	loadUserByUsername(String email);
}
