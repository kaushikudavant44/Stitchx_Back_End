package com.mavericksoft.stitchx.security.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mavericksoft.stitchx.security.dto.SignupRequest;
import com.mavericksoft.stitchx.security.models.ERole;
import com.mavericksoft.stitchx.security.models.Role;
import com.mavericksoft.stitchx.security.models.User;
import com.mavericksoft.stitchx.security.repository.RoleRepository;
import com.mavericksoft.stitchx.security.repository.UserRepository;
import com.mavericksoft.stitchx.user.exception.RoleNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	RoleRepository roleRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return UserDetailsImpl.build(user);
	}

	@Transactional
	public User createNewUserAccount(SignupRequest signUpRequest) throws RoleNotFoundException {
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			throw new RoleNotFoundException("Error: Please specify user roles");
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "ROLE_ADMIN":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "ROLE_SHOP_OWNER":
					Role shopOwnerRole = roleRepository.findByName(ERole.ROLE_SHOP_OWNER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(shopOwnerRole);

					break;
				case "ROLE_SALESMAN":
					Role salesmanRole = roleRepository.findByName(ERole.ROLE_SALESMAN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(salesmanRole);

					break;
				case "ROLE_DELIVERY_BOY":
					Role deliveryBoy = roleRepository.findByName(ERole.ROLE_DELIVERY_BOY)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(deliveryBoy);
					break;
				case "ROLE_QUALITY_CHECKER":
					Role qualityChecker = roleRepository.findByName(ERole.ROLE_QUALITY_CHECKER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(qualityChecker);
					break;
				
				}
			});
		}

		user.setRoles(roles);
		user.setEnabled(Boolean.TRUE);
		userRepository.save(user);
		return user;
	}

	public User updateResetPasswordToken(String otp, String email) {
		
		Optional<User> user = userRepository.findByEmail(email);
		
		if(user.isPresent()) {
			user.get().setResetPasswordToken(otp);
			return userRepository.save(user.get());
		}
		return null;
	}

	public Optional<User> getByResetPasswordToken(String token) {
		
		return userRepository.findByResetPasswordToken(token);
	}

}
