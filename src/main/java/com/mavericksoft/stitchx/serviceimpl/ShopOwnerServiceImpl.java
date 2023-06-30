package com.mavericksoft.stitchx.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.ShopOwner;
import com.mavericksoft.stitchx.repository.ShopOwnerServiceRepository;
import com.mavericksoft.stitchx.service.ShopOwnerService;

@Service
public class ShopOwnerServiceImpl implements ShopOwnerService {

	@Autowired
	private ShopOwnerServiceRepository shopOwnerServiceRepository;

	@Autowired
	UserDetailsService userServiceImpl;


	@Override
	public ShopOwner save(ShopOwner shopOwner) {
		shopOwner.setStatus(Status.ACTIVE);
		return shopOwnerServiceRepository.save(shopOwner);
	}

	@Override
	public List<ShopOwner> getAllShopDetails() {

		return shopOwnerServiceRepository.findAllByStatus(Status.ACTIVE);
	}

	@Override
	public List<ShopOwner> getAllShopDetails(String username) {
		return shopOwnerServiceRepository.findByUsername(username);
	}

	@Override
	public ShopOwner getAllShopDetailsByShopId(Long shopId) {
		return shopOwnerServiceRepository.findByShopId(shopId);
	}

}
