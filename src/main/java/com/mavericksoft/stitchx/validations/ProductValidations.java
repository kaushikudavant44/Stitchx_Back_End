package com.mavericksoft.stitchx.validations;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.service.ProductService;



@Component
public class ProductValidations {
	
	@Autowired
	ProductService productService;
	
	public Boolean isProductAvailable(String productName, TokenType tokenType) {
		
		Product product=productService.getProductByNameAndTokenType(productName,tokenType);
		if(Objects.isNull(product)) {
			return false;
		}
		return true;	
	}

	public Boolean isProductAvailable(Long productId) {
		
		Product product=productService.getProductById(productId);
		if(Objects.isNull(product)) {
			return false;
		}
		return true;
	}

}
