package in.fincase.restcontroller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.fincase.dto.MashitlaResponseDto;
import in.fincase.service.IpoAllotmentService;
import jakarta.websocket.server.PathParam;

@RequestMapping("/ipo")
@RestController
public class IppoAllotmentStatusChecker {

	@Autowired
	IpoAllotmentService ipoAllotmentService;
	
	@GetMapping("/maashitla/{ipoName}")
	public ResponseEntity<List<MashitlaResponseDto>> checkIpoStatus(@PathVariable(value = "ipoName") String ipoName ){
		
		  try {
	            // Fetch IPO allotment details
	            List<MashitlaResponseDto> responseDtos = ipoAllotmentService.checkIpoAllotment(ipoName);

	            // Return the response with HTTP 200 (OKs)
	            return ResponseEntity.ok(responseDtos);
	        } catch (Exception e) {
	            // Log the exception
	            e.printStackTrace();

	            // Return HTTP 500 (Internal Server Error) if any exception occurs
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	}
