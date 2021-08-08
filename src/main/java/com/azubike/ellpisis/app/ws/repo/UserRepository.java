package com.azubike.ellpisis.app.ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.azubike.ellpisis.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);

	// using preset query methods
	UserEntity findByUserId(String userId);

}
