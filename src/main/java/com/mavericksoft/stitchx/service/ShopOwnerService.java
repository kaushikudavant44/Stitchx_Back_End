package com.mavericksoft.stitchx.service;

import java.util.List;

import com.mavericksoft.stitchx.models.ShopOwner;

public interface ShopOwnerService {

	ShopOwner save(ShopOwner shopOwner);

	List<ShopOwner> getAllShopDetails(String username);

	List<ShopOwner> getAllShopDetails();

	ShopOwner getAllShopDetailsByShopId(Long shopId);
	
	

}
