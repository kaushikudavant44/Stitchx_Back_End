package com.mavericksoft.stitchx.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.models.ShopOwner;
import com.mavericksoft.stitchx.repository.SalesmanRepository;
import com.mavericksoft.stitchx.security.models.ERole;
import com.mavericksoft.stitchx.security.models.User;
import com.mavericksoft.stitchx.security.repository.UserRepository;
import com.mavericksoft.stitchx.service.SalesmanService;
import com.mavericksoft.stitchx.service.ShopOwnerService;
import com.mavericksoft.stitchx.user.exception.UserExistsException;

@Service
public class SalesmanServiceImpl implements SalesmanService{
	
	@Autowired
	SalesmanRepository salesmanRepository;
	
	@Autowired
	ShopOwnerService shopownerService;
	
	@Autowired
	UserRepository userRepository;
	
	private static final String ROLE_SALESMAN = "ROLE_SALESMAN";

	@Override
	public Salesman saveSalesmanDetails(Salesman salesman) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<ShopOwner> allShopDetails = shopownerService.getAllShopDetails(userDetails.getUsername());
		salesman.setShopId(allShopDetails.get(0));
		return salesmanRepository.save(salesman);
	}

	@Override
	public List<Salesman> getShopSalesmans() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 List<Salesman> salesmans = new ArrayList<>();
		 
		 boolean anyMatch = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_SALESMAN));
		 
		 if(anyMatch) {
			Salesman salesman = salesmanRepository.findByUsernameAndSalesmanStatus(userDetails.getUsername(), Status.ACTIVE);
			salesmans.add(salesman);
		 }else {
			 List<ShopOwner> shopDetails = shopownerService.getAllShopDetails(userDetails.getUsername());
			 salesmans =  salesmanRepository.findByShopIdAndSalesmanStatus(shopDetails.get(0), Status.ACTIVE);
		 }
		
		return salesmans;
	}

	@Override
	public Salesman getSalesmanById(Long salesmanId) {
		Optional<Salesman> salesman = salesmanRepository.findById(salesmanId);
		if(salesman.isPresent()) {
			return salesman.get();
		}
		
		return null;
	}

	@Override
	public Salesman getSalesmanByUsername(String username) {
		return salesmanRepository.findByUsername(username);
	}

	@Override
	public Salesman deactivateSalesman(String username) throws UserExistsException {
		
		Optional<User> user = userRepository.findByUsername(username);
		
		boolean salesmanUser = user.get().getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_SALESMAN));
		if(user.isPresent() && salesmanUser) {
			user.get().setEnabled(Boolean.FALSE);
			user.get().setUsername(username+Constant.DEACTIVATED);
			User deactivatedUser = userRepository.save(user.get());
			if(Objects.nonNull(deactivatedUser)) {
				Salesman deactivatedSalesman = salesmanRepository.findByUsername(username);
				deactivatedSalesman.setSalesmanStatus(Status.INACTIVE);
				salesmanRepository.save(deactivatedSalesman);
				return deactivatedSalesman;
			}
		}
		 throw new UserExistsException("Salesman Not Exist with this username");
	}

}
