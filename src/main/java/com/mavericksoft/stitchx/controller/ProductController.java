package com.mavericksoft.stitchx.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.ProductType;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.service.ProductService;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.validations.ProductValidations;

@RestController
@RequestMapping(Constant.API)
@CrossOrigin
public class ProductController {

	@Autowired
	ProductService productService;

	@Autowired
	ProductValidations productValidation;

	@PreAuthorize(Constant.ACCESS_ADMIN)
	@PostMapping("product")
	public ResponseEntity<Product> addNewProduct(@RequestParam("amount") Double amount,
			@RequestParam("type") TokenType tokenType, @RequestParam("productName") Products productName, @RequestParam("productType") ProductType productType)
			throws ProductExistsException {

		if (Boolean.TRUE.equals(productValidation.isProductAvailable(productName.name(), tokenType))) {
			throw new ProductExistsException("Product already exist! Please select different Product or Type");
		}
		Product product = new Product();
		product.setTokenAmount(amount);
		product.setTokenType(TokenType.valueOf(String.valueOf(tokenType)));
		product.setProductName(productName.name());
		product.setProductStatus(Status.ACTIVE);
		product.setProductType(ProductType.valueOf(String.valueOf(productType)));
		return new ResponseEntity<>(productService.addNewProduct(product), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_DELIVERY_BOY_AND_QUALITY_CHECKER)
	@GetMapping("products")
	public ResponseEntity<List<Product>> getActiveProducts() {

		return new ResponseEntity<>(productService.getActiveProducts(), HttpStatus.OK);
	}
	
	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	@GetMapping("product/{name}/{tokenType}")
	public ResponseEntity<Product> getActiveProducts(@PathVariable("name") Products product,@PathVariable("tokenType") TokenType tokenType) {

		return new ResponseEntity<>(productService.getProductByNameAndTokenType(product.name(),tokenType), HttpStatus.OK);
	}
	
	
	
	

}
