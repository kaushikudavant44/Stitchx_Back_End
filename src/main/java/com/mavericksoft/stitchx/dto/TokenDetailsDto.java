package com.mavericksoft.stitchx.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.enums.TokenType;

import lombok.Data;

@Data
public class TokenDetailsDto {
	
	private Integer totalToken;
	
	private Integer remainingToken;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	TokenType tokenType; 

	String product;
}
