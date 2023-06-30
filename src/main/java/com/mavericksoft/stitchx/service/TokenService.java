package com.mavericksoft.stitchx.service;

import java.io.IOException;
import java.util.List;

import com.google.zxing.WriterException;
import com.mavericksoft.stitchx.dto.TokenDetailsDto;
import com.mavericksoft.stitchx.dto.TokenOrderDTO;
import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.GeneratedTokens;
import com.mavericksoft.stitchx.models.TokenOrder;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.user.exception.TokenDoesNotExist;

public interface TokenService {

	TokenOrder tokenOrder(TokenOrderDTO tokenOrder) throws ProductExistsException;
	
	List<TokenOrder> getAllTokens(String username);
	
	TokenOrder useToken(TokenType tokenType, Long productId) throws TokenDoesNotExist;

	TokenOrder placeTokenOrder(Long tokenOrderId);

	List<GeneratedTokens> generateTokens(Long quantity, Products product) throws WriterException, IOException;

	List<TokenDetailsDto> getShopTokenDetails(String username);

	List<TokenOrder> getShopRequestedTokens(PaymentStatus paymentStatus);

}
