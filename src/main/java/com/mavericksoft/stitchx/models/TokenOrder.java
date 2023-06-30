/**
 * 
 */
package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.PaymentOptions;
import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.enums.TokenOrderStatus;
import com.mavericksoft.stitchx.enums.TokenType;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Table
@Entity
@Data
public class TokenOrder extends AuditModel {

	private static final long serialVersionUID = 1332358878222867667L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column
	@NotNull
	Long productId;

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
	@Enumerated(EnumType.STRING)
	PaymentOptions paymentBy;

	@Column
	@NotNull
	Double totalAmount;

	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	PaymentStatus paymentStatus;
	
	@Column
	@NotNull
	String username;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	TokenOrderStatus tokenOrderStatus;

	public Double calculateTotalTokensAmount() {

		return tokenAmount * quantity;
	}

}
