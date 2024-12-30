package in.fincase.serviceimpl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.fincase.dto.FamilyMembersDto;
import in.fincase.dto.FamilyResponseDto;
import in.fincase.entity.FamilyMembersEntity;
import in.fincase.entity.UserEntity;
import in.fincase.mapper.FamilyMemberMapper;
import in.fincase.repository.FamilyMEmberRepository;
import in.fincase.repository.UserRepository;
import in.fincase.service.FamilyMembersOperationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
public class FamilyMembersServiceImpl implements FamilyMembersOperationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyMEmberRepository familyMembersRepository;

    @Autowired
    private FamilyMemberMapper familyMembersMapper;

    @Override
    public FamilyResponseDto addFamilyMember(FamilyMembersDto familyMemberDTO, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMembersEntity familyMember = familyMembersMapper.toEntity(familyMemberDTO);
        familyMember.setUser(user);

        FamilyMembersEntity savedFamilyMember = familyMembersRepository.save(familyMember);

        return familyMembersMapper.toDto(savedFamilyMember);
    }

    @Override
    public List<FamilyResponseDto> getFamilyMembers(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));


        List<FamilyMembersEntity> familyMembers = familyMembersRepository.findByUser(user);
    return familyMembers.stream()
                .map(familyMembersMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FamilyMembersDto updateFamilyMember(Long familyMemberId, FamilyMembersDto familyMemberDTO, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMembersEntity familyMember = familyMembersRepository.findById(familyMemberId)
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        if (!familyMember.getUser().equals(user)) {
            throw new RuntimeException("Family member does not belong to the current user");
        }

        familyMember.setName(familyMemberDTO.getName());
        familyMember.setPanNumber(familyMemberDTO.getPanNumber());

        FamilyMembersEntity updatedFamilyMember = familyMembersRepository.save(familyMember);

        return familyMembersMapper.toReqDto(updatedFamilyMember);
    }

    @Override
    public void deleteFamilyMember(Long familyMemberId, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMembersEntity familyMember = familyMembersRepository.findById(familyMemberId)
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        if (!familyMember.getUser().equals(user)) {
            throw new RuntimeException("Family member does not belong to the current user");
        }

        familyMembersRepository.delete(familyMember);
    }
}