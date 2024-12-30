package in.fincase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.fincase.entity.UserEntity;
import java.util.List;


@Repository
public interface UserRepository  extends JpaRepository<UserEntity, Long>{
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findById(Long id);
}
