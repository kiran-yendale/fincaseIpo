package in.fincase.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public  class Userdto {

	 @NotBlank(message = "Email cannot be blank")
	    @Email(message = "Email must be valid")
	    private String email;

	    @NotBlank(message = "Password cannot be blank")
	    @Size(min = 8, message = "Password must be at least 8 characters")
	    private String password;

}
