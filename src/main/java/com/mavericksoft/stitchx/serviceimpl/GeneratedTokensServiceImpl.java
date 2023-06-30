/**
 * 
 */
package com.mavericksoft.stitchx.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.models.GeneratedTokens;
import com.mavericksoft.stitchx.repository.GeneratedTokensRepository;
import com.mavericksoft.stitchx.service.GeneratedTokensService;

/**
 * @author ADMIN
 *
 */
@Service
public class GeneratedTokensServiceImpl implements GeneratedTokensService {

	@Autowired
	GeneratedTokensRepository generatedTokensRepository; 
	
	@Override
	public GeneratedTokens saveToken(GeneratedTokens generatedToken) {
		return generatedTokensRepository.save(generatedToken);
	}

	@Override
	public GeneratedTokens getLastRecord() {
		return generatedTokensRepository.findTopByOrderByIdDesc();
	}

}
