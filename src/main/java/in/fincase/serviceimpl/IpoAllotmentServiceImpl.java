package in.fincase.serviceimpl;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.fincase.dto.MashitlaResponseDto;
import in.fincase.entity.FamilyMembersEntity;
import in.fincase.entity.UserEntity;
import in.fincase.repository.FamilyMEmberRepository;
import in.fincase.repository.UserRepository;
import in.fincase.service.IpoAllotmentService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;




@Service
@Transactional
public class IpoAllotmentServiceImpl implements IpoAllotmentService{

	@Autowired 
	UserRepository userRepository;
	
	@Autowired
	FamilyMEmberRepository familyMembersrepo;
	
	@Autowired
    private IPOResultScraper ipoResultScraper; // Assume you refactor IPOResultScraper into a bean for dependency injection

	
	@Override
	public ArrayList<MashitlaResponseDto> checkIpoAllotment(String ipoName) 	{
	
	String email=SecurityContextHolder.getContext().getAuthentication().getName();
	System.out.println("finding email"+email);
	
	Optional<UserEntity> user=userRepository.findByEmail(email);
			System.out.println("getting user id using mail");
		          Long id=user.get().getId();
		  		System.out.println("user id retrived "+id);         
	          List<FamilyMembersEntity> familyList=familyMembersrepo.findByUserId(id);
	  		
	          System.out.println("finding family members associted with that id");
	  			List<String> panNumbers = familyList.stream()
	  		        .map(FamilyMembersEntity::getPanNumber)
	  		        .collect(Collectors.toList()); 
	  	
	  		System.out.println(panNumbers);
	  		ArrayList<MashitlaResponseDto> allotmentResults = new ArrayList<>();

	        // Iterate through each PAN number and check IPO allotment status
	        for (String pan : panNumbers) {
	            try {
	            	System.out.println(ipoName+" "+pan);
	                // Call IPOResultScraper's method to get results for the current PAN
	                Map<String, String> result = ipoResultScraper.checkIpoAllotment(ipoName, pan);

	                // Map the result to an IpoAllotmentDTO
	                MashitlaResponseDto allotmentDTO = new MashitlaResponseDto();
	         
	                allotmentDTO.setIpoName(ipoName);
	                allotmentDTO.setName(result.get("Name")); 
	                allotmentDTO.setApplicationNo(result.get("Application No")); 
	                allotmentDTO.setShareApplied(result.get("Share Applied")); 
	                allotmentDTO.setShareAlloted(result.get("Share Alloted")); 
	                allotmentDTO.setPan(pan);	                
	                
	                
	                allotmentResults.add(allotmentDTO);
	            } catch (Exception e) {
	                System.err.println("Error checking allotment for PAN " + pan + ": " + e.getMessage());
	            }
	        }

	        return allotmentResults;
	    }
	}