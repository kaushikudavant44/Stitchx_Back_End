package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	List<Product> findAllByProductStatus(Status active);

	Product findByIdAndTokenType(Long productId, TokenType tokenType);

	Product findByProductNameAndTokenType(String productName, TokenType tokenType);

	List<Product> findByProductName(String product);

}
