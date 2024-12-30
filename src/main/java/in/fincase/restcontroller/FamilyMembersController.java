package in.fincase.restcontroller;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.fincase.dto.FamilyMembersDto;
import in.fincase.dto.FamilyResponseDto;
import in.fincase.service.FamilyMembersOperationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/member")
@RestController
public class FamilyMembersController {
	 @Autowired
	    private FamilyMembersOperationService familyMembersOperationService;

	    @PostMapping("/add")
	    public ResponseEntity<FamilyResponseDto> addFamilyMember(@RequestBody FamilyMembersDto familyMembersDto) {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        FamilyResponseDto addedFamilyMember = familyMembersOperationService.addFamilyMember(familyMembersDto, email);
	        return new ResponseEntity<>(addedFamilyMember, HttpStatus.CREATED);
	    }

	  
	    @GetMapping("allmembers")
	    public ResponseEntity<List<FamilyResponseDto>> getFamilyMembers() {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        List<FamilyResponseDto> familyMembers = familyMembersOperationService.getFamilyMembers(email);
	        return new ResponseEntity<>(familyMembers, HttpStatus.OK);
	    }

	    // Endpoint to update a family member's details
	    @PutMapping("/{familyMemberId}")
	    public ResponseEntity<FamilyMembersDto> updateFamilyMember(
	            @PathVariable Long familyMemberId, 
	            @RequestBody FamilyMembersDto familyMembersDto) {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        FamilyMembersDto updatedFamilyMember = familyMembersOperationService.updateFamilyMember(familyMemberId, familyMembersDto, email);
	        return new ResponseEntity<>(updatedFamilyMember, HttpStatus.OK);
	    }

	    // Endpoint to delete a family member
	    @DeleteMapping("/{familyMemberId}")
	    public ResponseEntity<Void> deleteFamilyMember(@PathVariable Long familyMemberId) {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        familyMembersOperationService.deleteFamilyMember(familyMemberId, email);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }
}
