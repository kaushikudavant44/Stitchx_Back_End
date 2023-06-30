package com.mavericksoft.stitchx.service;

import com.mavericksoft.stitchx.models.GeneratedTokens;

public interface GeneratedTokensService {

	GeneratedTokens saveToken(GeneratedTokens generatedToken);

	GeneratedTokens getLastRecord();

}
