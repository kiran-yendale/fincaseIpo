package in.fincase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import in.fincase.dto.Userdto;
import in.fincase.entity.UserEntity;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	
	UserEntity toEntity(Userdto userdto);

    Userdto toDto(UserEntity userEntity);
}

