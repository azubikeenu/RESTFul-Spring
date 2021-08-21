package com.azubike.ellpisis.app.ws.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.azubike.ellpisis.app.ws.io.entity.AddressEntity;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

}
