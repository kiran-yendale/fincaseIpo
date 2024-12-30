package in.fincase.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import in.fincase.dto.LinkinTimeDTO;
import in.fincase.serviceimpl.LinkinTimeServiceImpl;

@RestController
public class LinkinTimeController {

	@Autowired
	LinkinTimeServiceImpl linkinTimeServiceImpl;;
	
	@GetMapping("/linkintime/{ipoName}")
	public ResponseEntity<List<LinkinTimeDTO>> getIpoStatus(@PathVariable("ipoName") String ipoName){
		
		  List<LinkinTimeDTO> ipoDetails = linkinTimeServiceImpl.getIpoStatus(ipoName);

	        // Return ResponseEntity with the data and HTTP status
	        return ResponseEntity.ok(ipoDetails);
	   	}
}
