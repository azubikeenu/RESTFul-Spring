package com.azubike.ellpisis.app.ws.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.azubike.ellpisis.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);

	// using preset query methods
	UserEntity findByUserId(String userId);

	UserEntity findUserByEmailVerificationToken(String token);

	@Query(value = "select * from USERS U  where U.EMAIL_VERIFICATION_STATUS = true ", countQuery = "select count(*) from USERS U  where U.EMAIL_VERIFICATION_STATUS = true ", nativeQuery = true)
	Page<UserEntity> findVerifiedUsers(Pageable pageableRequest);

	@Query(value = "select * from Users u where u.first_name = ?1", nativeQuery = true)

	List<UserEntity> findUserByFirstName(String firstName);

}
