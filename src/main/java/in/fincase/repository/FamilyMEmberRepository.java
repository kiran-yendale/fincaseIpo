package in.fincase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import in.fincase.entity.FamilyMembersEntity;
import in.fincase.entity.UserEntity;

import java.util.List;

@Repository
public interface FamilyMEmberRepository  extends JpaRepository<FamilyMembersEntity, Long>{
	 List<FamilyMembersEntity> findByUser(UserEntity user);
	List <FamilyMembersEntity> findByUserId(Long userId);
}
