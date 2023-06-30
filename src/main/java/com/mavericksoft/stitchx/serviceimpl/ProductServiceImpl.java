package com.mavericksoft.stitchx.serviceimpl;



import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.repository.MeasurementRepository;
import com.mavericksoft.stitchx.repository.ProductRepository;
import com.mavericksoft.stitchx.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	MeasurementRepository meausurementRepository;

	@Override
	public Product getProductById(Long productId) {
		
		Optional<Product> product = productRepository.findById(productId);
		if(product.isPresent())
		{
		return product.get();
		}
		return null;
	}

	@Override
	@Transactional
	public Product addNewProduct(Product product) {
		Product newProduct = productRepository.save(product);				
		return newProduct;
	}

	@Override
	public List<Product> getActiveProducts() {
		return productRepository.findAllByProductStatus(Status.ACTIVE);
	}

	@Override
	public Product getProductDetailsByProductIdAndTokenType(Long productId, TokenType tokenType) {
		return productRepository.findByIdAndTokenType(productId,tokenType);
	}

	@Override
	public Product getProductByNameAndTokenType(String productName, TokenType tokenType) {
		return productRepository.findByProductNameAndTokenType(productName,tokenType);
	}

	@Override
	public List<Product> getProductByName(Products product) {
		
		return productRepository.findByProductName(product.name());
	}

}
