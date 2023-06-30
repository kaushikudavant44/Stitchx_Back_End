/*
 * package com.mavericksoft.stitchx.serviceimpl;
 * 
 * import java.util.List; import java.util.Set;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Service;
 * 
 * import com.mavericksoft.stitchx.enums.TokenType; import
 * com.mavericksoft.stitchx.models.TokenDetails; import
 * com.mavericksoft.stitchx.repository.TokenDetailsRepository; import
 * com.mavericksoft.stitchx.service.TokenDetailService;
 * 
 * @Service public class TokenDetailServiceImpl implements TokenDetailService{
 * 
 * @Autowired TokenDetailsRepository tokenDetailsRepository;
 * 
 * @Override public List<TokenDetails> saveTokenDetails(Set<TokenDetails>
 * tokenDetails) {
 * 
 * return tokenDetailsRepository.saveAll(tokenDetails); }
 * 
 * @Override public List<TokenDetails> getAllTokens() { return
 * tokenDetailsRepository.findAll(); }
 * 
 * @Override public TokenDetails getProductDetailsByProductIdAndTokenType(Long
 * productId, TokenType tokenType) { return
 * tokenDetailsRepository.findByProductIdAndTokenType(productId,tokenType); }
 * 
 * @Override public List<TokenDetails> findTokenDetailsByProductId(Long id) {
 * return tokenDetailsRepository.findTokenDetailsByProductId(id); }
 * 
 * }
 */