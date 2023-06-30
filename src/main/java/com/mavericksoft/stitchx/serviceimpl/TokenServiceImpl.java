package com.mavericksoft.stitchx.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mavericksoft.stitchx.dto.TokenDetailsDto;
import com.mavericksoft.stitchx.dto.TokenOrderDTO;
import com.mavericksoft.stitchx.enums.PaymentOptions;
import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.TokenOrderStatus;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.GeneratedTokens;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.models.ShopOwner;
import com.mavericksoft.stitchx.models.Token;
import com.mavericksoft.stitchx.models.TokenOrder;
import com.mavericksoft.stitchx.repository.TokenOrderRepository;
import com.mavericksoft.stitchx.repository.TokenRepository;
import com.mavericksoft.stitchx.service.GeneratedTokensService;
import com.mavericksoft.stitchx.service.ProductService;
import com.mavericksoft.stitchx.service.ShopOwnerService;
import com.mavericksoft.stitchx.service.TokenService;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.user.exception.TokenDoesNotExist;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	TokenOrderRepository tokenOrderRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	ProductService productService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private GeneratedTokensService generatedTokensService;

	@Value("${classpath.uploadImage}")
	private String classPath;

	@Autowired
	ShopOwnerService shopOwnerService;
	
	@Override
	public TokenOrder tokenOrder(TokenOrderDTO tokenOrderDto) throws ProductExistsException {

		Product product = productService.getProductByNameAndTokenType(tokenOrderDto.getProductName().name(),
				tokenOrderDto.getTokenType());

		if (Objects.isNull(product)) {
			throw new ProductExistsException("Product does not exist");
		}

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		TokenOrder tokenOrder = modelMapper.map(tokenOrderDto, TokenOrder.class);

		tokenOrder.setProductId(product.getId());

		tokenOrder.setUsername(userDetails.getUsername());

		tokenOrder.setTokenAmount(product.getTokenAmount());

		tokenOrder.setTotalAmount(tokenOrder.calculateTotalTokensAmount());

		tokenOrder.setTokenOrderStatus(TokenOrderStatus.ACTIVE);

		tokenOrder.setTokenType(product.getTokenType());

		tokenOrder.setPaymentStatus(PaymentStatus.PENDING);

		tokenOrder.setPaymentBy(tokenOrder.getPaymentBy());

		Set<Token> tokens = new HashSet<>();

		TokenOrder newTokenOrder = tokenOrderRepository.save(tokenOrder);

		for (int i = 0; i < newTokenOrder.getQuantity(); i++) {
			Token tokenDetails = new Token();
			tokenDetails.setTokenId(UUID.randomUUID().toString());
			tokenDetails.setIsUsed(false);
			tokenDetails.setUsername(userDetails.getUsername());
			tokenDetails.setTokenOrder(newTokenOrder);
			tokenDetails.setPaymentStatus(PaymentStatus.PENDING);
			tokens.add(tokenDetails);
		}
		tokenRepository.saveAll(tokens);

		return newTokenOrder;
	}

	@Override
	public List<TokenOrder> getAllTokens(String username) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return tokenOrderRepository.findByUsername(userDetails.getUsername());
	}

	@Override
	public TokenOrder useToken(TokenType tokenType, Long productId) throws TokenDoesNotExist {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		TokenOrder tokenOrders = tokenOrderRepository
				.findTopByUsernameAndTokenTypeAndProductIdAndTokenOrderStatusAndPaymentStatus(userDetails.getUsername(),
						tokenType, productId, TokenOrderStatus.ACTIVE, PaymentStatus.PAID);

		if (Objects.nonNull(tokenOrders)) {
			Token token = tokenRepository.findTopByTokenOrderAndIsUsedAndPaymentStatus(tokenOrders, false,
					PaymentStatus.PAID);
			if (Objects.nonNull(token)) {
				token.setIsUsed(true);
				tokenRepository.save(token);
			} else {
				tokenOrders.setTokenOrderStatus(TokenOrderStatus.INACTIVE);
				tokenOrderRepository.save(tokenOrders);
				throw new TokenDoesNotExist("Token does not exist please buy token and order again");
			}
		} else {
			throw new TokenDoesNotExist("Token does not exist please buy token and order again");
		}
		return tokenOrders;
	}

	@Override
	public TokenOrder placeTokenOrder(Long tokenOrderId) {

		Optional<TokenOrder> tokenDetails = tokenOrderRepository.findById(tokenOrderId);

		if (tokenDetails.isPresent() && tokenDetails.get().getPaymentBy().equals(PaymentOptions.COD)) {

			tokenDetails.get().setPaymentStatus(PaymentStatus.PAID);
			List<Token> tokens = tokenRepository.findByTokenOrder(tokenDetails.get());
			for (Token token : tokens) {
				token.setPaymentStatus(PaymentStatus.PAID);
				tokenRepository.save(token);
			}
		}

		return tokenDetails.get();
	}

	@Override
	public List<GeneratedTokens> generateTokens(Long quantity, Products product) throws WriterException, IOException {

		List<GeneratedTokens> generatedTokens=new ArrayList<>();
		Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();

		hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		for (Long l = 0L; l < quantity; l++) {
			GeneratedTokens generatedToken = new GeneratedTokens();
			generatedToken.setProductName(product.name());
			String createQRData = createQRData(product.name().substring(0).toUpperCase());
			String imagePath = classPath+createQRData + ".png";
			generatedToken.setQrData(createQRData);
			generatedToken.setImagePath(imagePath);
			createQR(createQRData, imagePath, "UTF-8", hashMap, 200, 200);
			generatedTokens.add(generatedTokensService.saveToken(generatedToken));

		}

		return generatedTokens;
	}

	private String createQRData(String productIntialLetter) {

		GeneratedTokens lastRecord = generatedTokensService.getLastRecord();
		if (Objects.isNull(lastRecord)) {
			return productIntialLetter + 00;
		}
		return productIntialLetter + lastRecord.getId();
	}

	private static void createQR(String data, String path, String charset, Map<EncodeHintType, ErrorCorrectionLevel> hashMap, int height, int width)
			throws WriterException, IOException {

		BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, width, height);

		MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
	}

	@Override
	public List<TokenDetailsDto> getShopTokenDetails(String username) {
	
		List<TokenDetailsDto> tokenDetailsDtoList = new ArrayList<>();
		
		List<TokenOrder> tokenOrders= tokenOrderRepository.findByUsernameAndPaymentStatus(username,PaymentStatus.PAID);
		
		Map<@NotNull Long, List<TokenOrder>> regularTokensByProduct = tokenOrders.stream().collect(Collectors.groupingBy(t -> t.getProductId()));
		
		for(Entry<Long,List<TokenOrder>> regularTokenOrder: regularTokensByProduct.entrySet()) {
			TokenDetailsDto regularTokenDetailsDto = new TokenDetailsDto();
			List<Long> tokenIds = regularTokenOrder.getValue().stream().map(TokenOrder :: getId).collect(Collectors.toList());
			List<Token> tokens = tokenRepository.findByTokenOrderIdInAndIsUsed(tokenIds,false);
			regularTokenDetailsDto.setTotalToken(regularTokenOrder.getValue().stream().mapToInt(r -> r.getQuantity()).sum());
			regularTokenDetailsDto.setRemainingToken(tokens.size());
			Optional<@NotNull TokenType> tokenType = regularTokenOrder.getValue().stream().map(t -> t.getTokenType()).findFirst();
			regularTokenDetailsDto.setTokenType(tokenType.get());
			regularTokenDetailsDto.setProduct(productService.getProductById(regularTokenOrder.getKey()).getProductName());
			tokenDetailsDtoList.add(regularTokenDetailsDto);
		}
		
		return tokenDetailsDtoList;
	}

	@Override
	public List<TokenOrder> getShopRequestedTokens(PaymentStatus paymentStatus) {
		List<ShopOwner> shops = shopOwnerService.getAllShopDetails();
		List<@NotNull String> shopUsernames = shops.stream().map(ShopOwner :: getUsername).collect(Collectors.toList());
		
		return tokenOrderRepository.findByUsernameInAndPaymentStatusOrderById(shopUsernames,paymentStatus); 
		
	}

}
