package com.azubike.ellpisis.app.ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.azubike.ellpisis.app.ws.io.entity.PasswordRestTokenEntity;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordRestTokenEntity, Long> {

}
