package com.mavericksoft.stitchx.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.ShopOwnerDto;
import com.mavericksoft.stitchx.models.EmailDetails;
import com.mavericksoft.stitchx.models.ShopOwner;
import com.mavericksoft.stitchx.security.dto.SignupRequest;
import com.mavericksoft.stitchx.security.models.ERole;
import com.mavericksoft.stitchx.security.models.User;
import com.mavericksoft.stitchx.security.repository.UserRepository;
import com.mavericksoft.stitchx.security.response.MessageResponse;
import com.mavericksoft.stitchx.security.services.UserDetailsServiceImpl;
import com.mavericksoft.stitchx.service.EmailService;
import com.mavericksoft.stitchx.service.ShopOwnerService;
import com.mavericksoft.stitchx.user.exception.RoleNotFoundException;
import com.mavericksoft.stitchx.utilities.Utility;

@RestController
@RequestMapping(Constant.API + "shopowner")
@CrossOrigin
public class ShopOwnerController {

	private static final Logger log = LoggerFactory.getLogger(ShopOwnerController.class);

	@Autowired
	ShopOwnerService shopOwnerService;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	UserDetailsServiceImpl userServiceImpl;

	@Autowired
	Utility utility;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	EmailService emailService;

	@PreAuthorize(Constant.ACCESS_ADMIN)
	@PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addNewShop(@RequestPart(value = "file", required = false) MultipartFile file,
			@ModelAttribute("shopOwner") @Valid ShopOwnerDto shopOwnerDto, HttpServletRequest request)
			throws RoleNotFoundException {

		if (Boolean.TRUE.equals(userRepository.existsByUsername(shopOwnerDto.getMobileNumber()))) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (Boolean.TRUE.equals(userRepository.existsByEmail(shopOwnerDto.getEmail()))) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		SignupRequest signupRequest = new SignupRequest();
		Set<String> role = new HashSet<>();
		role.add(ERole.ROLE_SHOP_OWNER.name());
		signupRequest.setEmail(shopOwnerDto.getEmail());
		signupRequest.setUsername(shopOwnerDto.getMobileNumber());
		signupRequest.setRole(role);
		signupRequest.setPassword(shopOwnerDto.getPassword());
		User registeredShopOwner = userServiceImpl.createNewUserAccount(signupRequest);

		ShopOwner shopOwner = modelMapper.map(shopOwnerDto, ShopOwner.class);

		if (Objects.nonNull(registeredShopOwner)) {
			shopOwner.setUsername(registeredShopOwner.getUsername());
			ShopOwner registeredShop = shopOwnerService.save(shopOwner);
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setMsgBody("Welcome To Stitch, You are officially partner with stitchx.");
			emailDetails.setSubject("Welcome To Stitchx");
			emailDetails.setRecipient(shopOwnerDto.getEmail());
			String status = emailService.sendSimpleMail(emailDetails);
			log.info("email status is = " + status);
			return new ResponseEntity<>(registeredShop, HttpStatus.OK);
		}

		if (null != file && !file.isEmpty()) {

			try {
				shopOwner.setShopPhotoPath(utility.getSiteURL(request) + utility.uploadFile(file));
			} catch (Exception e) {
				log.error(e.getMessage());
			}

		}
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@PreAuthorize(Constant.ACCESS_ADMIN_AND_DELIVERY_BOY)
	@GetMapping("/shops")
	public ResponseEntity<List<ShopOwner>> getUser() {

		return new ResponseEntity<>(shopOwnerService.getAllShopDetails(), HttpStatus.OK);
	}

}
