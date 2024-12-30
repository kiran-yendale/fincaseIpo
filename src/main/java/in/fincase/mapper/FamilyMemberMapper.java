package in.fincase.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;

import in.fincase.dto.FamilyMembersDto;
import in.fincase.dto.FamilyResponseDto;
import in.fincase.entity.FamilyMembersEntity;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {
	
	FamilyMembersEntity toEntity(FamilyMembersDto familyDto);
	
	   FamilyResponseDto toDto(FamilyMembersEntity familyEntity);
	  
	   FamilyMembersDto toReqDto(FamilyMembersEntity familyEntity);

}
