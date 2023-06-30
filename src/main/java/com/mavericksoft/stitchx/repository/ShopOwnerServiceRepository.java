package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.ShopOwner;

public interface ShopOwnerServiceRepository extends JpaRepository<ShopOwner, Long>{

	List<ShopOwner> findByUsername(String username);

	ShopOwner findByShopId(Long shopId);

	List<ShopOwner> findAllByStatus(Status status);

}
