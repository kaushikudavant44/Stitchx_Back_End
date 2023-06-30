/**
 * 
 */
package com.mavericksoft.stitchx.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.TokenDetailsDto;
import com.mavericksoft.stitchx.dto.TokenOrderDTO;
import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.models.TokenOrder;
import com.mavericksoft.stitchx.service.TokenService;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;

import io.swagger.v3.oas.annotations.Operation;

/**
 * @author kaushikudavant
 *
 */
@RestController
@RequestMapping(Constant.API)
@CrossOrigin
public class TokenController {

	@Autowired
	TokenService tokenService;

	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	@PostMapping("token/order")
	public ResponseEntity<TokenOrder> orderTokens(@RequestBody TokenOrderDTO tokenOrderDTO)
			throws ProductExistsException {

		return new ResponseEntity<>(tokenService.tokenOrder(tokenOrderDTO), HttpStatus.OK);
	}

	@Operation(tags = "admin-controller")
	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	@PutMapping("token/order/placed/{tokenOrderId}")
	public ResponseEntity<TokenOrder> orderTokens(@PathVariable("tokenOrderId") Long tokenOrderId) {

		return new ResponseEntity<>(tokenService.placeTokenOrder(tokenOrderId), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	@GetMapping("/tokens")
	public ResponseEntity<List<TokenOrder>> getAllTokens() {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(tokenService.getAllTokens(userDetails.getUsername()), HttpStatus.OK);
	}
	
	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	@GetMapping("/tokens/details")
	public ResponseEntity<List<TokenDetailsDto>> getTokenDetails(@RequestParam String username) {

		return new ResponseEntity<>(tokenService.getShopTokenDetails(username), HttpStatus.OK);
	}
	
	@PreAuthorize(Constant.ACCESS_ADMIN)
	@GetMapping("/shop/requested/tokens")
	@Operation(tags = "admin-controller")
	public ResponseEntity<List<TokenOrder>> getShopRequestedTokens(@RequestParam PaymentStatus paymentStatus) {

		return new ResponseEntity<>(tokenService.getShopRequestedTokens(paymentStatus), HttpStatus.OK);
	}

}
