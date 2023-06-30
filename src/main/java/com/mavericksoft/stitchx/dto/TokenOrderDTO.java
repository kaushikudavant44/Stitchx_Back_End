package com.mavericksoft.stitchx.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.enums.PaymentOptions;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.TokenType;

import lombok.Data;
@Data
public class TokenOrderDTO {
	
	@Column
	@NotNull
	@Enumerated(EnumType.STRING)
	Products productName;

	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	TokenType tokenType;

	@Column
	@NotNull
	Double tokenAmount;

	@Column
	@NotNull
	Integer quantity;

	@Column
	@NotNull
	PaymentOptions paymentBy;
	
}
