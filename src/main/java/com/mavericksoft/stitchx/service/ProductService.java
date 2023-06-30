/**
 * 
 */
package com.mavericksoft.stitchx.service;

import java.util.List;

import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Product;

/**
 * @author kaushikudavant
 *
 */
public interface ProductService {

	Product addNewProduct(Product product);
	
	Product getProductById(Long productId);

	List<Product> getActiveProducts();

	Product getProductDetailsByProductIdAndTokenType(Long productId, TokenType tokenType);

	Product getProductByNameAndTokenType(String productName, TokenType tokenType);

	List<Product> getProductByName(Products product);

}
