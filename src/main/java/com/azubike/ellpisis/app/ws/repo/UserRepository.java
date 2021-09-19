package com.azubike.ellpisis.app.ws.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

	@Query(value = "select * from Users u where u.last_name= :lastName", nativeQuery = true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);

	@Query(value = "select * from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%", nativeQuery = true)
	List<UserEntity> findUserByKeyWord(@Param("keyword") String keyword);

	@Query(value = "select u.first_name,u.last_name  from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%", nativeQuery = true)
	List<Object[]> firstUserFirstNameAndLastNameByKeyWord(@Param("keyword") String keyword);

	@Transactional
	@Modifying
	@Query(value = "update Users u set u.email_verification_status = :emailVerificationStatus where u.user_id = :userId", nativeQuery = true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerficationStatus,
			@Param("userId") String userId);

	@Query("from UserEntity WHERE userId = :userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);

	@Query("SELECT u.firstName, u.lastName FROM UserEntity u WHERE  u.userId  = :userId")
	List<Object[]> getUserEntityFullName(@Param("userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "update UserEntity u set u.emailVerificationStatus = :emailVerificationStatus where u.userId = :userId")
	void updateEmailVerificationEntity(@Param("emailVerificationStatus") boolean emailVerficationStatus,
			@Param("userId") String userId);

}
