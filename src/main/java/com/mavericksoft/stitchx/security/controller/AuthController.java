package com.mavericksoft.stitchx.security.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.PersonalDetailsDto;
import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.models.ShopOwner;
import com.mavericksoft.stitchx.security.dto.LoginRequest;
import com.mavericksoft.stitchx.security.dto.SignupRequest;
import com.mavericksoft.stitchx.security.jwt.JwtUtils;
import com.mavericksoft.stitchx.security.models.User;
import com.mavericksoft.stitchx.security.repository.RoleRepository;
import com.mavericksoft.stitchx.security.repository.UserRepository;
import com.mavericksoft.stitchx.security.response.JwtResponse;
import com.mavericksoft.stitchx.security.response.MessageResponse;
import com.mavericksoft.stitchx.security.services.UserDetailsImpl;
import com.mavericksoft.stitchx.security.services.UserDetailsServiceImpl;
import com.mavericksoft.stitchx.service.SalesmanService;
import com.mavericksoft.stitchx.service.ShopOwnerService;
import com.mavericksoft.stitchx.serviceimpl.EmailServiceImpl;
import com.mavericksoft.stitchx.user.exception.RoleNotFoundException;
import com.mavericksoft.stitchx.user.exception.UserExistsException;
import com.mavericksoft.stitchx.utilities.Utility;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	ShopOwnerService shopOwnerService;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	SalesmanService salesmanService;
	
	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws UserExistsException {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		
		if(user.isPresent() && !user.get().isEnabled()) {
			throw new UserExistsException("User de-activated from system");
		}
		
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_DELIVERY_BOY_AND_QUALITY_CHECKER)
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_DELIVERY_BOY_AND_QUALITY_CHECKER)
	@PostMapping("/change/{old}/password/{new}")
	public ResponseEntity<?> changeUserPassword(@PathVariable("old") String oldPassword,
			@PathVariable("new") String newPassword) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		if (!checkIfValidOldPassword(user.get(), oldPassword)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Old password is incorrect!"));
		}
		user.get().setPassword(encoder.encode(newPassword));
		User changePasswordUser = userRepository.save(user.get());
		logoutUser();
		return ResponseEntity.ok(changePasswordUser);
	}

	public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
		return encoder.matches(oldPassword, user.getPassword());
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_DELIVERY_BOY_AND_QUALITY_CHECKER)
	@GetMapping("/user")
	public ResponseEntity<PersonalDetailsDto> getPersonalDetails(HttpServletRequest request) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		PersonalDetailsDto personalDetailsDto = new PersonalDetailsDto();
		roles.forEach(role -> {
			switch (role) {
			case "ROLE_ADMIN":
				personalDetailsDto.setMobile(user.get().getUsername());
				personalDetailsDto.setEmail(user.get().getEmail());
				break;
			case "ROLE_SHOP_OWNER":
				List<ShopOwner> shopDetails = shopOwnerService.getAllShopDetails(userDetails.getUsername());
				personalDetailsDto.setMobile(user.get().getUsername());
				personalDetailsDto.setEmail(user.get().getEmail());
				personalDetailsDto.setAddress(shopDetails.get(0).getAddress());
				personalDetailsDto.setName(shopDetails.get(0).getOwnerName());
				personalDetailsDto.setShopName(shopDetails.get(0).getShopName());
				personalDetailsDto.setImage(Utility.getSiteURL(request) + shopDetails.get(0).getShopPhotoPath());
				break;
			case "ROLE_SALESMAN":
				personalDetailsDto.setMobile(user.get().getUsername());
				personalDetailsDto.setEmail(user.get().getEmail());
				Salesman salesmanByUsername = salesmanService.getSalesmanByUsername(userDetails.getUsername());
				ShopOwner shopOwner = shopOwnerService
						.getAllShopDetailsByShopId(salesmanByUsername.getShopId().getShopId());
				personalDetailsDto.setName(salesmanByUsername.getName());
				personalDetailsDto.setShopName(shopOwner.getShopName());
				personalDetailsDto.setAddress(salesmanByUsername.getAddress());
				personalDetailsDto.setImage(Utility.getSiteURL(request) + salesmanByUsername.getProfilePic());
				break;
			case "ROLE_DELIVERY_BOY":
				personalDetailsDto.setMobile(user.get().getUsername());
				personalDetailsDto.setEmail(user.get().getEmail());
				break;
			case "ROLE_QUALITY_CHECKER":
				personalDetailsDto.setMobile(user.get().getUsername());
				personalDetailsDto.setEmail(user.get().getEmail());
				break;
			}
		});

		return new ResponseEntity<>((personalDetailsDto), HttpStatus.OK);

	}

	@PreAuthorize(Constant.ACCESS_ADMIN)
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws RoleNotFoundException, UserExistsException {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new UserExistsException("Error: Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new UserExistsException("Error: Email is already taken!");
		}

		return ResponseEntity.ok(userDetailsService.createNewUserAccount(signUpRequest));
	}
	
	@PostMapping("/forgot_password")
	public ResponseEntity<String> processForgotPassword(HttpServletRequest request, @RequestParam String email) throws UnsupportedEncodingException, MessagingException {
	  
	    String otp = getRandomNumberString();
	     
	    User updateResetPasswordTokenUser = userDetailsService.updateResetPasswordToken(otp, email);
		if(Objects.nonNull(updateResetPasswordTokenUser)) {
			emailServiceImpl.sendEmail(email, otp);
		}
		return ResponseEntity.ok("OTP is shared on your mail please check your mail "+email);
	}
	
	public static String getRandomNumberString() {
	    // It will generate 6 digit random Number.
	    // from 0 to 999999
	    Random rnd = new Random();
	    int number = rnd.nextInt(999999);

	    // this will convert any number sequence into 6 character.
	    return String.format("%06d", number);
	}
	
	@PostMapping("/reset_password")
	public  ResponseEntity<?> processResetPassword(@RequestParam String password, @RequestParam String otp) {
	   
	     
		Optional<User> user = userDetailsService.getByResetPasswordToken(otp);	     
	    if (user.isPresent()) {
	    	user.get().setPassword(encoder.encode(password));
			userRepository.save(user.get());
	    } else {           
	    	return ResponseEntity.badRequest().body(new MessageResponse("Error: Your token is not valid!"));
	    }
	     
	    return ResponseEntity.ok("Password change susscessfully!");
	}
}