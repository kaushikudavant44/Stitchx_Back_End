package com.mavericksoft.stitchx.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.SalesmanDTO;
import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.security.dto.SignupRequest;
import com.mavericksoft.stitchx.security.models.ERole;
import com.mavericksoft.stitchx.security.models.User;
import com.mavericksoft.stitchx.security.services.UserDetailsServiceImpl;
import com.mavericksoft.stitchx.service.SalesmanService;
import com.mavericksoft.stitchx.user.exception.RoleNotFoundException;
import com.mavericksoft.stitchx.user.exception.UserExistsException;
import com.mavericksoft.stitchx.utilities.Utility;

@RestController
@RequestMapping(Constant.API)
@CrossOrigin
public class SalesmanController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private SalesmanService salesmanService;

	@Autowired
	Utility utility;

	@PreAuthorize(Constant.ACCESS_SHOPOWNER)
	@PostMapping(value = "register/salesman", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addSalesman(@RequestPart(name="file",required = false) MultipartFile file,
			@ModelAttribute("salesman") SalesmanDTO salesmanDTO, HttpServletRequest request) throws RoleNotFoundException {
				
		SignupRequest signupRequest=new SignupRequest();
		Set<String> role=new HashSet<>();
		role.add(ERole.ROLE_SALESMAN.name());
		signupRequest.setEmail(salesmanDTO.getEmail());
		signupRequest.setUsername(salesmanDTO.getMobileNumber());
		signupRequest.setRole(role);
		signupRequest.setPassword(salesmanDTO.getPassword());
		User registeredSalesman = userDetailsService.createNewUserAccount(signupRequest);
		if(Objects.nonNull(registeredSalesman)) {
			Salesman salesman=modelMapper.map(salesmanDTO,Salesman.class);
			salesman.setUsername(registeredSalesman.getUsername());
			if(null != file && !file.isEmpty()) {
			
			try {
				salesman.setProfilePic(Utility.getSiteURL(request)+utility.uploadFile(file));
			} catch (Exception e) {
				e.getMessage();
			}
			
		}
			if(Objects.nonNull(salesmanService.saveSalesmanDetails(salesman))){
				return new ResponseEntity<>(Constant.SALESMAN_REGISTER_MSG,HttpStatus.OK);
			}
		}	
		return new ResponseEntity<>(Constant.SALESMAN_REGISTRATION_FAILED,HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN)
	@GetMapping("shop/salesmans")
	public ResponseEntity<List<Salesman>> getShopSalesmans(){
		
		return new ResponseEntity<>(salesmanService.getShopSalesmans(),HttpStatus.OK);
	}
	
	@PreAuthorize(Constant.ACCESS_SHOPOWNER)
	@PutMapping("deactivate/salesman/{username}")
	public ResponseEntity<Salesman> deactivateSalesman(@PathVariable String username) throws UserExistsException{
		return new ResponseEntity<>(salesmanService.deactivateSalesman(username),HttpStatus.OK);
	}

}
