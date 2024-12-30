package in.fincase.service;

import java.util.List;


import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

import in.fincase.dto.FamilyMembersDto;
import in.fincase.dto.FamilyResponseDto;

@Service
public interface FamilyMembersOperationService {
	 FamilyResponseDto addFamilyMember(FamilyMembersDto familyMemberDTO, String email);

	    List<FamilyResponseDto> getFamilyMembers(String email);

	    FamilyMembersDto updateFamilyMember(Long familyMemberId, FamilyMembersDto familyMemberDTO, String email);

	    void deleteFamilyMember(Long familyMemberId, String email); 
}
